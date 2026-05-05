package com.example.spellcoach.presentation.navigation

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PracticeListHolder @Inject constructor() {
    @Volatile
    var lastListId: Long? = null
}
