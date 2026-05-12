package com.itclimb.spellcoach.core.navigation

object AppNav {

    const val TAB_LISTS = "tab/lists"
    const val TAB_PRACTICE = "tab/practice"
    const val TAB_SETTINGS = "tab/settings"

    private const val LISTS_HOME = "home"
    private const val LISTS_ADD = "addwords?listId={listId}"
    private const val LISTS_MANAGE_WORDS = "managewords?listId={listId}"

    private const val PRACTICE_ENTRY = "practice_entry"
    private const val PRACTICE_SESSION = "session/{listId}"
    private const val PRACTICE_RESULTS = "results"

    private const val SETTINGS_LEAF = "settings_screen"

    fun listsAddWords(listId: Long = -1L): String = "addwords?listId=$listId"

    fun listsManageWords(listId: Long): String = "managewords?listId=$listId"

    fun practiceSession(listId: Long): String = "session/$listId"

    object Lists {
        const val HOME = LISTS_HOME
        const val ADD_WORDS = LISTS_ADD
        const val MANAGE_WORDS = LISTS_MANAGE_WORDS
    }

    object Practice {
        const val ENTRY = PRACTICE_ENTRY
        const val SESSION = PRACTICE_SESSION
        const val RESULTS = PRACTICE_RESULTS
    }

    object Settings {
        const val SCREEN = SETTINGS_LEAF
    }
}
