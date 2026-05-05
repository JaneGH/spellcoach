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
        val guess = attempt.trim()
        val target = word.text.trim()
        val isCorrect = guess.equals(target, ignoreCase = true)
        val updated = if (isCorrect) {
            val nextCorrect = word.correctCount + 1
            val mastered = nextCorrect >= requiredCorrectStreak
            word.copy(
                correctCount = nextCorrect,
                isMastered = mastered
            )
        } else {
            when (mistakeBehavior) {
                MistakeBehavior.DECREASE_PROGRESS -> {
                    val next = (word.correctCount - 1).coerceAtLeast(0)
                    word.copy(correctCount = next, isMastered = false)
                }
                MistakeBehavior.RESET_PROGRESS -> {
                    word.copy(correctCount = 0, isMastered = false)
                }
            }
        }
        wordRepository.updateWord(updated)
        return SpellingProcessResult(
            isSpellingCorrect = isCorrect,
            updatedWord = updated
        )
    }
}
