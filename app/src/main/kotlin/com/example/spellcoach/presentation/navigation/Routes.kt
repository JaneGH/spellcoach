package com.example.spellcoach.presentation.navigation

sealed class Route(val path: String) {
    data object WordLists : Route("wordlists")
    data object AddWords : Route("addwords?listId={listId}") {
        fun createNew() = "addwords"
        fun edit(listId: Long) = "addwords?listId=$listId"
    }
    data object Practice : Route("practice/{listId}") {
        fun create(listId: Long) = "practice/$listId"
    }
    data object Results : Route("results")
    data object Settings : Route("settings")
}

