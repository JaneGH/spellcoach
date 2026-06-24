package com.itclimb.spellcoach.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.itclimb.spellcoach.domain.model.MistakeBehavior
import com.itclimb.spellcoach.domain.model.Word
import com.itclimb.spellcoach.testing.FakeWordRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ProcessSpellingResultUseCaseTest {

    private val repo = FakeWordRepository()
    private val useCase = ProcessSpellingResultUseCase(repo)

    private fun word(correctCount: Int = 0, incorrectCount: Int = 0) = Word(
        id = 1L,
        listId = 1L,
        text = "apple",
        correctCount = correctCount,
        incorrectCount = incorrectCount,
        isMastered = false,
    )

    @Test
    fun correctAnswer_incrementsCorrectCount() = runTest {
        val result = useCase(
            word = word(correctCount = 1),
            attempt = "apple",
            requiredCorrectStreak = 3,
            mistakeBehavior = MistakeBehavior.DECREASE_PROGRESS,
        )

        assertThat(result.isSpellingCorrect).isTrue()
        assertThat(result.updatedWord.correctCount).isEqualTo(2)
        assertThat(result.updatedWord.incorrectCount).isEqualTo(0)
        assertThat(repo.lastUpdatedWord?.correctCount).isEqualTo(2)
    }

    @Test
    fun wrongAnswer_decreaseProgress_reducesCorrectCountByOne() = runTest {
        val result = useCase(
            word = word(correctCount = 2),
            attempt = "aple",
            requiredCorrectStreak = 3,
            mistakeBehavior = MistakeBehavior.DECREASE_PROGRESS,
        )

        assertThat(result.isSpellingCorrect).isFalse()
        assertThat(result.updatedWord.correctCount).isEqualTo(1)
        assertThat(result.updatedWord.incorrectCount).isEqualTo(1)
    }

    @Test
    fun wrongAnswer_decreaseProgress_doesNotGoBelowZero() = runTest {
        val result = useCase(
            word = word(correctCount = 0),
            attempt = "aple",
            requiredCorrectStreak = 3,
            mistakeBehavior = MistakeBehavior.DECREASE_PROGRESS,
        )

        assertThat(result.isSpellingCorrect).isFalse()
        assertThat(result.updatedWord.correctCount).isEqualTo(0)
        assertThat(result.updatedWord.incorrectCount).isEqualTo(1)
    }

    @Test
    fun wrongAnswer_resetProgress_setsCorrectCountToZero() = runTest {
        val result = useCase(
            word = word(correctCount = 2),
            attempt = "aple",
            requiredCorrectStreak = 3,
            mistakeBehavior = MistakeBehavior.RESET_PROGRESS,
        )

        assertThat(result.isSpellingCorrect).isFalse()
        assertThat(result.updatedWord.correctCount).isEqualTo(0)
        assertThat(result.updatedWord.incorrectCount).isEqualTo(1)
    }

    @Test
    fun wrongAnswer_resetProgress_fromZero_staysAtZero() = runTest {
        val result = useCase(
            word = word(correctCount = 0),
            attempt = "aple",
            requiredCorrectStreak = 3,
            mistakeBehavior = MistakeBehavior.RESET_PROGRESS,
        )

        assertThat(result.isSpellingCorrect).isFalse()
        assertThat(result.updatedWord.correctCount).isEqualTo(0)
        assertThat(result.updatedWord.incorrectCount).isEqualTo(1)
    }
}
