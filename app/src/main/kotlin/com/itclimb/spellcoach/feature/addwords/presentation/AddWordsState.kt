package com.itclimb.spellcoach.feature.addwords.presentation

data class AddWordsState(
    val listId: Long? = null,
    val isEditMode: Boolean = false,
    val listName: String = "",
    val rawInput: String = "",
    val previewWords: List<String> = emptyList(),
    val isImporting: Boolean = false,
    val saving: Boolean = false,
    val errorMessage: String? = null,
    val importNotice: String? = null,
)

sealed interface AddWordsEvent {
    data object Saved : AddWordsEvent
}

