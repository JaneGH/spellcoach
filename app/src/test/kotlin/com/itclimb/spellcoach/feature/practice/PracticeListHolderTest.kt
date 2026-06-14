package com.itclimb.spellcoach.feature.practice

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PracticeListHolderTest {

    @Test
    fun isExplicitSessionReady_trueWhenNoPendingRequest() {
        val holder = PracticeListHolder()

        assertThat(holder.isExplicitSessionReady(1L)).isTrue()
        assertThat(holder.isExplicitSessionReady(99L)).isTrue()
    }

    @Test
    fun isExplicitSessionReady_matchesPendingListOnly() {
        val holder = PracticeListHolder().apply { pendingPracticeListId = 2L }

        assertThat(holder.isExplicitSessionReady(1L)).isFalse()
        assertThat(holder.isExplicitSessionReady(2L)).isTrue()
    }

    @Test
    fun clearPendingIfMatches_clearsOnlyMatchingList() {
        val holder = PracticeListHolder().apply { pendingPracticeListId = 2L }

        holder.clearPendingIfMatches(1L)
        assertThat(holder.pendingPracticeListId).isEqualTo(2L)

        holder.clearPendingIfMatches(2L)
        assertThat(holder.pendingPracticeListId).isNull()
    }
}
