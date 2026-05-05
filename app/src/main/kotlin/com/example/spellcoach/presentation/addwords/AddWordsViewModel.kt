package com.example.spellcoach.presentation.addwords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spellcoach.domain.usecase.CreateWordListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class AddWordsUiState(
    val listName: String = "",
    val rawInput: String = "",
    val previewWords: List<String> = emptyList(),
    val saving: Boolean = false,
    val errorMessage: String? = null
)

sealed interface AddWordsEvent {
    data object Saved : AddWordsEvent
}

@HiltViewModel
class AddWordsViewModel @Inject constructor(
    private val createWordList: CreateWordListUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddWordsUiState())
    val state: StateFlow<AddWordsUiState> = _state.asStateFlow()

    private val _events = Channel<AddWordsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun setListName(value: String) {
        _state.value = _state.value.copy(listName = value, errorMessage = null)
    }

    fun setRawInput(value: String) {
        val words = parseWords(value)
        _state.value = _state.value.copy(rawInput = value, previewWords = words, errorMessage = null)
    }

    fun addParsedWordsFromInput() {
        val words = parseWords(_state.value.rawInput)
        if (words.isEmpty()) return
        val merged = (_state.value.previewWords + words).distinct()
        _state.value = _state.value.copy(previewWords = merged, rawInput = "", errorMessage = null)
    }

    fun removeWord(word: String) {
        _state.value = _state.value.copy(
            previewWords = _state.value.previewWords.filter { it != word }
        )
    }

    fun save() {
        viewModelScope.launch {
            _state.value = _state.value.copy(saving = true, errorMessage = null)
            val result = createWordList(_state.value.listName, _state.value.previewWords)
            result.fold(
                onSuccess = {
                    _state.value = _state.value.copy(saving = false)
                    _events.send(AddWordsEvent.Saved)
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        saving = false,
                        errorMessage = when (e.message) {
                            "empty_name" -> "Please enter a list name."
                            "no_words" -> "Add at least one word."
                            else -> e.message ?: "Could not save."
                        }
                    )
                }
            )
        }
    }

    private fun parseWords(input: String): List<String> =
        input.split(Regex("[,\\s]+"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
}
