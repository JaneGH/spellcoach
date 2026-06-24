package com.itclimb.spellcoach.feature.practice

import com.google.common.truth.Truth.assertThat
import com.itclimb.spellcoach.testing.FakeLastPracticeListStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PracticeListHolderTest {

    @Test
    fun isExplicitSessionReady_trueWhenNoPendingRequest() {
        val holder = PracticeListHolder(FakeLastPracticeListStore())

        assertThat(holder.isExplicitSessionReady(1L)).isTrue()
        assertThat(holder.isExplicitSessionReady(99L)).isTrue()
    }

    @Test
    fun isExplicitSessionReady_matchesPendingListOnly() {
        val holder = PracticeListHolder(FakeLastPracticeListStore()).apply {
            pendingPracticeListId = 2L
        }

        assertThat(holder.isExplicitSessionReady(1L)).isFalse()
        assertThat(holder.isExplicitSessionReady(2L)).isTrue()
    }

    @Test
    fun clearPendingIfMatches_clearsOnlyMatchingList() {
        val holder = PracticeListHolder(FakeLastPracticeListStore()).apply {
            pendingPracticeListId = 2L
        }

        holder.clearPendingIfMatches(1L)
        assertThat(holder.pendingPracticeListId).isEqualTo(2L)

        holder.clearPendingIfMatches(2L)
        assertThat(holder.pendingPracticeListId).isNull()
    }

    @Test
    fun setLastPracticeListId_persistsToStore() = runTest {
        val store = FakeLastPracticeListStore()
        val holder = PracticeListHolder(store)

        holder.setLastPracticeListId(42L)

        assertThat(store.lastPracticeListId.first()).isEqualTo(42L)
    }

    @Test
    fun lastPracticeListId_restoredAfterHolderRecreation() = runTest {
        val store = FakeLastPracticeListStore()
        val holder1 = PracticeListHolder(store)
        holder1.setLastPracticeListId(42L)

        val holder2 = PracticeListHolder(store)

        assertThat(holder2.lastPracticeListId.first()).isEqualTo(42L)
    }

    @Test
    fun pendingPracticeListId_isNotPersisted() = runTest {
        val store = FakeLastPracticeListStore()
        val holder = PracticeListHolder(store).apply {
            pendingPracticeListId = 7L
        }

        assertThat(store.lastPracticeListId.first()).isNull()

        val recreated = PracticeListHolder(store)
        assertThat(recreated.pendingPracticeListId).isNull()
        assertThat(recreated.lastPracticeListId.first()).isNull()
    }
}
