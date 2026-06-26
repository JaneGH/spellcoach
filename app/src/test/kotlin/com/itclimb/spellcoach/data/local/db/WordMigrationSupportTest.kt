package com.itclimb.spellcoach.data.local.db

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class WordMigrationSupportTest {

    @Test
    fun mergeRows_mergesCaseDuplicatesAndKeepsBestProgress() {
        val merged = WordMigrationSupport.mergeRows(
            listOf(
                MigrationWordRow(
                    id = 1L,
                    listId = 10L,
                    text = "Cat",
                    correctCount = 3,
                    incorrectCount = 1,
                    isMastered = true,
                    masteredAt = 100L,
                ),
                MigrationWordRow(
                    id = 2L,
                    listId = 10L,
                    text = "cat",
                    correctCount = 0,
                    incorrectCount = 2,
                    isMastered = false,
                    masteredAt = null,
                ),
            )
        )

        assertThat(merged).hasSize(1)
        val row = merged.single()
        assertThat(row.keeperId).isEqualTo(1L)
        assertThat(row.text).isEqualTo("cat")
        assertThat(row.correctCount).isEqualTo(3)
        assertThat(row.incorrectCount).isEqualTo(3)
        assertThat(row.isMastered).isTrue()
        assertThat(row.masteredAt).isEqualTo(100L)
        assertThat(row.deleteIds).containsExactly(2L)
    }

    @Test
    fun mergeRows_leavesDistinctWordsSeparate() {
        val merged = WordMigrationSupport.mergeRows(
            listOf(
                MigrationWordRow(1L, 10L, "cat", 0, 0, false, null),
                MigrationWordRow(2L, 10L, "dog", 0, 0, false, null),
            )
        )

        assertThat(merged).hasSize(2)
        assertThat(merged.map { it.text }).containsExactly("cat", "dog")
        assertThat(merged.all { it.deleteIds.isEmpty() }).isTrue()
    }
}
