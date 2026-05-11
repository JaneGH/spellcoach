package com.example.spellcoach.domain.usecase

import com.example.spellcoach.domain.model.MistakeBehavior
import com.example.spellcoach.domain.model.Word
import com.example.spellcoach.domain.repository.WordRepository
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

        val updated = if (isCorrect) {
            val nextCorrect = (word.correctCount + 1).coerceAtMost(required)
            val mastered = nextCorrect >= required
            val nextMasteredAt =
                if (mastered) (word.masteredAt ?: now) else null

            word.copy(
                correctCount = nextCorrect,
                incorrectCount = word.incorrectCount,
                isMastered = mastered,
                masteredAt = nextMasteredAt
            )
        } else {
            // Always decrement on wrong attempts; never reset to 0.
            val nextCorrect = (word.correctCount - 1).coerceAtLeast(0)
            val mastered = nextCorrect >= required
            val nextMasteredAt =
                if (mastered) (word.masteredAt ?: now) else null

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
