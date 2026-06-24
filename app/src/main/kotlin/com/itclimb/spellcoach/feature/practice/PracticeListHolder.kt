package com.itclimb.spellcoach.feature.practice

import com.itclimb.spellcoach.data.settings.LastPracticeListStore
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

/** Remembers the last list chosen for practice, persisted across process death. */
@Singleton
class PracticeListHolder @Inject constructor(
    private val lastPracticeListStore: LastPracticeListStore
) {
    val lastPracticeListId: Flow<Long?> = lastPracticeListStore.lastPracticeListId

    @Volatile
    var pendingPracticeListId: Long? = null

    suspend fun setLastPracticeListId(id: Long) {
        lastPracticeListStore.setLastPracticeListId(id)
    }

    suspend fun clearLastPracticeListId() {
        lastPracticeListStore.clearLastPracticeListId()
    }

    /** True when [sessionListId] matches an explicit pending request, or no request is pending. */
    fun isExplicitSessionReady(sessionListId: Long): Boolean {
        val pending = pendingPracticeListId ?: return true
        return pending == sessionListId
    }

    fun clearPendingIfMatches(sessionListId: Long) {
        if (pendingPracticeListId == sessionListId) {
            pendingPracticeListId = null
        }
    }
}
