package com.example.spellcoach.presentation.addwords

data class AddWordsState(
    val listName: String = "",
    val rawInput: String = "",
    val previewWords: List<String> = emptyList(),
    val isImporting: Boolean = false,
    val saving: Boolean = false,
    val errorMessage: String? = null
)

sealed interface AddWordsEvent {
    data object Saved : AddWordsEvent
}

