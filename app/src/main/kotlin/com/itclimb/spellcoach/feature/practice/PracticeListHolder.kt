package com.itclimb.spellcoach.feature.practice

import javax.inject.Inject
import javax.inject.Singleton

/** Remembers the last list chosen for practice (process-scoped). */
@Singleton
class PracticeListHolder @Inject constructor() {
    @Volatile
    var lastListId: Long? = null

    @Volatile
    var pendingPracticeListId: Long? = null

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
