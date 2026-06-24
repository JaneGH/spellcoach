package com.itclimb.spellcoach.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class WordLearnedTest {

    @Test
    fun isLearnedAtThreshold_isMasteredTrue_countsAsLearned() {
        assertThat(
            isLearnedAtThreshold(
                isMastered = true,
                masteredAt = null,
                correctCount = 0,
                requiredCorrectAnswers = 3,
            )
        ).isTrue()
    }

    @Test
    fun isLearnedAtThreshold_masteredAtNotNull_countsAsLearned() {
        assertThat(
            isLearnedAtThreshold(
                isMastered = false,
                masteredAt = 1_000L,
                correctCount = 0,
                requiredCorrectAnswers = 3,
            )
        ).isTrue()
    }

    @Test
    fun isLearnedAtThreshold_correctCountAtThreshold_countsAsLearned() {
        assertThat(
            isLearnedAtThreshold(
                isMastered = false,
                masteredAt = null,
                correctCount = 3,
                requiredCorrectAnswers = 3,
            )
        ).isTrue()
    }

    @Test
    fun isLearnedAtThreshold_correctCountBelowThreshold_notLearned() {
        assertThat(
            isLearnedAtThreshold(
                isMastered = false,
                masteredAt = null,
                correctCount = 2,
                requiredCorrectAnswers = 3,
            )
        ).isFalse()
    }

    @Test
    fun isLearnedAtThreshold_requiredLessThanOne_coercedToOne() {
        assertThat(
            isLearnedAtThreshold(
                isMastered = false,
                masteredAt = null,
                correctCount = 1,
                requiredCorrectAnswers = 0,
            )
        ).isTrue()

        assertThat(
            isLearnedAtThreshold(
                isMastered = false,
                masteredAt = null,
                correctCount = 0,
                requiredCorrectAnswers = -2,
            )
        ).isFalse()
    }

    @Test
    fun wordExtension_matchesTopLevelHelper() {
        val word = Word(
            id = 1L,
            listId = 1L,
            text = "cat",
            correctCount = 3,
            incorrectCount = 0,
            isMastered = false,
            masteredAt = null,
        )

        assertThat(word.isLearnedAtThreshold(3)).isTrue()
        assertThat(word.isLearnedAtThreshold(4)).isFalse()
    }
}
