package com.itclimb.spellcoach.data.mapper

import com.google.common.truth.Truth.assertThat
import com.itclimb.spellcoach.data.local.db.WordListWithProgress
import com.itclimb.spellcoach.data.local.entity.WordEntity
import com.itclimb.spellcoach.data.local.entity.WordListEntity
import org.junit.Test

class EntityMappersLearnedTest {

    @Test
    fun wordEntity_isLearned_matchesDomainThreshold() {
        val masteredByFlag = WordEntity(
            listId = 1L,
            text = "a",
            isMastered = true,
        )
        val masteredByTimestamp = WordEntity(
            listId = 1L,
            text = "b",
            masteredAt = 100L,
        )
        val masteredByCount = WordEntity(
            listId = 1L,
            text = "c",
            correctCount = 3,
        )
        val notMastered = WordEntity(
            listId = 1L,
            text = "d",
            correctCount = 2,
        )

        assertThat(masteredByFlag.isLearned(3)).isTrue()
        assertThat(masteredByTimestamp.isLearned(3)).isTrue()
        assertThat(masteredByCount.isLearned(3)).isTrue()
        assertThat(notMastered.isLearned(3)).isFalse()
    }

    @Test
    fun wordListWithProgress_toDomain_countsLearnedWordsUsingThreshold() {
        val progress = WordListWithProgress(
            list = WordListEntity(id = 1L, name = "Animals", createdAt = 0L),
            words = listOf(
                WordEntity(listId = 1L, text = "cat", correctCount = 3),
                WordEntity(listId = 1L, text = "dog", correctCount = 1),
                WordEntity(listId = 1L, text = "fox", isMastered = true),
            ),
        )

        val domain = progress.toDomain(requiredCorrectAnswers = 3)

        assertThat(domain.totalWords).isEqualTo(3)
        assertThat(domain.learnedWords).isEqualTo(2)
        assertThat(domain.isMastered).isFalse()
    }

    @Test
    fun wordListWithProgress_toDomain_requiredLessThanOne_coercedToOne() {
        val progress = WordListWithProgress(
            list = WordListEntity(id = 1L, name = "Animals", createdAt = 0L),
            words = listOf(
                WordEntity(listId = 1L, text = "cat", correctCount = 1),
                WordEntity(listId = 1L, text = "dog", correctCount = 0),
            ),
        )

        val domain = progress.toDomain(requiredCorrectAnswers = 0)

        assertThat(domain.learnedWords).isEqualTo(1)
    }
}
