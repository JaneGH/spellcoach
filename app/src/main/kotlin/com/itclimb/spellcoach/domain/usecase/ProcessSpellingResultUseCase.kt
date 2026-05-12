package com.itclimb.spellcoach.domain.usecase

import com.itclimb.spellcoach.domain.model.MistakeBehavior
import com.itclimb.spellcoach.domain.model.Word
import com.itclimb.spellcoach.domain.model.hasPersistedMastery
import com.itclimb.spellcoach.domain.repository.WordRepository
import javax.inject.Inject

data class SpellingProcessResult(
    val isSpellingCorrect: Boolean,
    val updatedWord: Word
)

class ProcessSpellingResultUseCase @Inject constructor(
    private val wordRepository: WordRepository
) {
    suspend operator fun invoke(
        word: Word,
        attempt: String,
        requiredCorrectStreak: Int,
        mistakeBehavior: MistakeBehavior
    ): SpellingProcessResult {
        // Student-friendly mastery rules:
        // - Correct: increment correctCount, capped at requiredCorrectStreak
        // - Wrong: decrement correctCount by 1 (never reset to 0), and increment incorrectCount
        val guess = attempt.trim()
        val target = word.text.trim()
        val isCorrect = guess.equals(target, ignoreCase = true)

        val required = requiredCorrectStreak.coerceAtLeast(1)
        val now = System.currentTimeMillis()
        val alreadyMastered = word.hasPersistedMastery()

        val updated = if (isCorrect) {
            val nextCorrect =
                if (alreadyMastered) {
                    word.correctCount + 1
                } else {
                    (word.correctCount + 1).coerceAtMost(required)
                }
            val mastered = alreadyMastered || (nextCorrect >= required)
            val nextMasteredAt = when {
                alreadyMastered -> word.masteredAt
                mastered -> word.masteredAt ?: now
                else -> null
            }

            word.copy(
                correctCount = nextCorrect,
                incorrectCount = word.incorrectCount,
                isMastered = mastered,
                masteredAt = nextMasteredAt
            )
        } else {
            // Always decrement on wrong attempts; never reset to 0.
            val nextCorrect = (word.correctCount - 1).coerceAtLeast(0)
            val mastered = alreadyMastered || (nextCorrect >= required)
            val nextMasteredAt = when {
                alreadyMastered -> word.masteredAt
                mastered -> word.masteredAt ?: now
                else -> null
            }

            // mistakeBehavior is intentionally ignored to keep behavior consistent with the practice loop.
            word.copy(
                correctCount = nextCorrect,
                incorrectCount = word.incorrectCount + 1,
                isMastered = mastered,
                masteredAt = nextMasteredAt
            )
        }
        wordRepository.updateWord(updated)
        return SpellingProcessResult(
            isSpellingCorrect = isCorrect,
            updatedWord = updated
        )
    }
}
