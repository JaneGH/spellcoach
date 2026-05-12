package com.itclimb.spellcoach.feature.practice

import javax.inject.Inject
import javax.inject.Singleton

/** Remembers the last list chosen for practice (process-scoped). */
@Singleton
class PracticeListHolder @Inject constructor() {
    @Volatile
    var lastListId: Long? = null
}
