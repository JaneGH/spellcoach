package com.example.spellcoach.feature.wordlists.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spellcoach.domain.model.WordList
import com.example.spellcoach.domain.repository.WordRepository
import com.example.spellcoach.domain.usecase.ObserveWordListsUseCase
import com.example.spellcoach.core.navigation.PracticeListHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WordListsUiState(
    val lists: List<WordList> = emptyList(),
    val loading: Boolean = true
)

@HiltViewModel
class WordListsViewModel @Inject constructor(
    observeWordLists: ObserveWordListsUseCase,
    private val wordRepository: WordRepository,
    private val practiceListHolder: PracticeListHolder
) : ViewModel() {

    fun rememberPracticeList(listId: Long) {
        practiceListHolder.lastListId = listId
    }

    val uiState: StateFlow<WordListsUiState> = observeWordLists()
        .map { WordListsUiState(lists = it, loading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WordListsUiState(loading = true)
        )

    fun resetListProgress(listId: Long) {
        viewModelScope.launch { wordRepository.resetProgress(listId) }
    }

    fun deleteList(listId: Long) {
        viewModelScope.launch { wordRepository.deleteWordList(listId) }
    }
}
