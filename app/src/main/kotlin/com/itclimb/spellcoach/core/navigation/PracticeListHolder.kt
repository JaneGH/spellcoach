package com.itclimb.spellcoach.core.navigation

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PracticeListHolder @Inject constructor() {
    @Volatile
    var lastListId: Long? = null
}
