package com.itclimb.spellcoach.feature.managewords.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itclimb.spellcoach.domain.speech.SpellCoachTextToSpeech
import com.itclimb.spellcoach.domain.model.Word
import com.itclimb.spellcoach.domain.model.isLearnedAtThreshold
import com.itclimb.spellcoach.domain.repository.DuplicateWordInListException
import com.itclimb.spellcoach.domain.repository.WordRepository
import com.itclimb.spellcoach.domain.usecase.ObserveSettingsUseCase
import com.itclimb.spellcoach.domain.usecase.ObserveWordsForListUseCase
import com.itclimb.spellcoach.domain.word.WordTextNormalizer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ManageWordsFilter { All, Practicing, Mastered }

data class ManageWordsUiState(
    val listIdValid: Boolean = false,
    val words: List<Word> = emptyList(),
    val requiredCorrectAnswers: Int = 3,
    val searchQuery: String = "",
    val filter: ManageWordsFilter = ManageWordsFilter.All,
    val multiSelectMode: Boolean = false,
    val selection: Set<Long> = emptySet(),
)

@HiltViewModel
class ManageWordsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeWordsForList: ObserveWordsForListUseCase,
    observeSettings: ObserveSettingsUseCase,
    private val wordRepository: WordRepository,
    private val textToSpeech: SpellCoachTextToSpeech,
) : ViewModel() {

    private val listId: Long = savedStateHandle.get<Long>("listId") ?: -1L

    private val local = MutableStateFlow(
        ManageWordsUiState(
            listIdValid = listId > 0,
        )
    )

    private val _events = Channel<ManageWordsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    val uiState: StateFlow<ManageWordsUiState> = combine(
        if (listId > 0) observeWordsForList(listId) else flowOf(emptyList()),
        observeSettings(),
        local
    ) { words, settings, l ->
        l.copy(
            listIdValid = listId > 0,
            words = words,
            requiredCorrectAnswers = settings.requiredCorrectAnswers,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ManageWordsUiState(listIdValid = listId > 0),
    )

    fun setSearchQuery(query: String) {
        local.update { it.copy(searchQuery = query) }
    }

    fun setFilter(filter: ManageWordsFilter) {
        local.update { it.copy(filter = filter) }
    }

    fun speakWord(text: String) {
        textToSpeech.speak(text)
    }

    fun toggleMastered(word: Word) {
        val r = uiState.value.requiredCorrectAnswers.coerceAtLeast(1)
        viewModelScope.launch {
            val mastered = word.isLearnedAtThreshold(r)
            val updated = if (mastered) {
                word.copy(
                    isMastered = false,
                    masteredAt = null,
                    correctCount = 0,
                )
            } else {
                word.copy(
                    isMastered = true,
                    masteredAt = System.currentTimeMillis(),
                    correctCount = maxOf(word.correctCount, r),
                )
            }
            wordRepository.updateWord(updated)
        }
    }

    fun resetWordProgress(wordId: Long) {
        viewModelScope.launch { wordRepository.resetWordProgress(wordId) }
    }

    fun deleteWord(wordId: Long) {
        viewModelScope.launch { wordRepository.deleteWord(wordId) }
    }

    fun renameWord(wordId: Long, newText: String) {
        viewModelScope.launch {
            val w = wordRepository.getWordById(wordId) ?: return@launch
            val normalized = WordTextNormalizer.normalize(newText)
            if (normalized == null) {
                _events.send(ManageWordsEvent.RenameInvalid)
                return@launch
            }
            if (normalized == w.text) {
                _events.send(ManageWordsEvent.RenameSucceeded)
                return@launch
            }
            runCatching {
                wordRepository.updateWord(w.copy(text = normalized))
            }.fold(
                onSuccess = { _events.send(ManageWordsEvent.RenameSucceeded) },
                onFailure = { error ->
                    if (error is DuplicateWordInListException) {
                        _events.send(ManageWordsEvent.RenameDuplicate)
                    }
                },
            )
        }
    }

    fun enterMultiSelect(wordId: Long) {
        local.update {
            it.copy(
                multiSelectMode = true,
                selection = setOf(wordId),
            )
        }
    }

    fun exitMultiSelect() {
        local.update {
            it.copy(multiSelectMode = false, selection = emptySet())
        }
    }

    fun toggleSelection(wordId: Long) {
        local.update { s ->
            val next = s.selection.toMutableSet()
            if (wordId in next) next.remove(wordId) else next.add(wordId)
            s.copy(selection = next)
        }
    }

    fun markSelectedMastered() {
        val ids = local.value.selection.toList()
        if (ids.isEmpty()) return
        val r = uiState.value.requiredCorrectAnswers.coerceAtLeast(1)
        viewModelScope.launch {
            ids.forEach { id ->
                val w = wordRepository.getWordById(id) ?: return@forEach
                wordRepository.updateWord(
                    w.copy(
                        isMastered = true,
                        masteredAt = System.currentTimeMillis(),
                        correctCount = maxOf(w.correctCount, r),
                    )
                )
            }
            exitMultiSelect()
        }
    }

    fun resetSelectedProgress() {
        val ids = local.value.selection.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            ids.forEach { wordRepository.resetWordProgress(it) }
            exitMultiSelect()
        }
    }

    fun deleteSelected() {
        val ids = local.value.selection.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            ids.forEach { wordRepository.deleteWord(it) }
            exitMultiSelect()
        }
    }
}

fun Word.displayStatus(requiredCorrect: Int): ManageWordStudyStatus {
    if (isLearnedAtThreshold(requiredCorrect)) return ManageWordStudyStatus.Mastered
    if (correctCount == 0 && incorrectCount == 0) return ManageWordStudyStatus.New
    return ManageWordStudyStatus.Practicing
}

enum class ManageWordStudyStatus { New, Practicing, Mastered }
