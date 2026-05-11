package com.example.spellcoach.presentation.navigation

object AppNav {

    const val TAB_LISTS = "tab/lists"
    const val TAB_PRACTICE = "tab/practice"
    const val TAB_SETTINGS = "tab/settings"

    private const val LISTS_HOME = "home"
    private const val LISTS_ADD = "addwords?listId={listId}"

    private const val PRACTICE_ENTRY = "practice_entry"
    private const val PRACTICE_SESSION = "session/{listId}"
    private const val PRACTICE_RESULTS = "results"

    private const val SETTINGS_LEAF = "settings_screen"

    fun listsHome(): String =
        LISTS_HOME

    fun listsAddWords(
        listId: Long = -1L
    ): String {
        return "addwords?listId=$listId"
    }

    fun practiceGraphRoot(): String =
        TAB_PRACTICE

    fun practiceEntry(): String =
        PRACTICE_ENTRY

    fun practiceSession(
        listId: Long
    ): String {
        return "session/$listId"
    }

    fun practiceResults(): String =
        PRACTICE_RESULTS

    fun settings(): String =
        SETTINGS_LEAF

    object Lists {
        const val HOME = LISTS_HOME
        const val ADD_WORDS = LISTS_ADD
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