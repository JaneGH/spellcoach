package com.itclimb.spellcoach.feature.managewords.presentation

sealed interface ManageWordsEvent {
    data object RenameSucceeded : ManageWordsEvent
    data object RenameDuplicate : ManageWordsEvent
    data object RenameInvalid : ManageWordsEvent
}
