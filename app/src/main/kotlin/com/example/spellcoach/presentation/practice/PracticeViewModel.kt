package com.example.spellcoach.presentation.practice

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spellcoach.data.practice.PracticeResultCache
import com.example.spellcoach.data.sound.SoundEffectPlayer
import com.example.spellcoach.data.tts.TtsManager
import com.example.spellcoach.domain.model.Badge
import com.example.spellcoach.domain.model.PracticeResult
import com.example.spellcoach.domain.model.Word
import com.example.spellcoach.domain.repository.RewardRepository
import com.example.spellcoach.domain.repository.WordRepository
import com.example.spellcoach.domain.usecase.ObserveSettingsUseCase
import com.example.spellcoach.domain.usecase.ProcessSpellingResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PracticeUiState(
    val listId: Long = 0L,
    val listName: String = "",
    val words: List<Word> = emptyList(),
    val currentIndex: Int = 0,
    val input: String = "",
    val letters: List<String> = emptyList(),
    val hintsEnabled: Boolean = true,
    val showHints: Boolean = false,
    val sessionCorrect: Int = 0,
    val incorrectSubmissions: Int = 0,
    val loading: Boolean = true,
    val feedbackCorrect: Boolean? = null,
    val animationHint: PracticeAnimHint = PracticeAnimHint.None,
    val audioEnabled: Boolean = true,
    val requiredCorrectAnswers: Int = 3,
    val wordJustMastered: Boolean = false
)

enum class PracticeAnimHint { None, BounceOk, ShakeWrong }

@HiltViewModel
class PracticeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val wordRepository: WordRepository,
    private val processSpelling: ProcessSpellingResultUseCase,
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val rewardRepository: RewardRepository,
    private val practiceResultCache: PracticeResultCache,
    private val sound: SoundEffectPlayer,
    private val tts: TtsManager
) : ViewModel() {

    private val listId: Long = savedStateHandle.get<Long>("listId") ?: 0L

    private val sessionBadges = mutableListOf<Badge>()

    private val _state = MutableStateFlow(PracticeUiState(listId = listId))
    val state: StateFlow<PracticeUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val name = wordRepository.getWordListName(listId).orEmpty()
            _state.update {
                it.copy(
                    listName = name,
                    loading = false,
                    showHints = false
                )
            }
        }
        viewModelScope.launch {
            val settingsFlow = observeSettingsUseCase()
            settingsFlow.collect { s ->
                _state.update { cur ->
                    cur.copy(
                        audioEnabled = s.audioEnabled,
                        hintsEnabled = s.letterHintsEnabled,
                        showHints = if (s.letterHintsEnabled) cur.showHints else false,
                        requiredCorrectAnswers = s.requiredCorrectAnswers
                    )
                }
                tts.setSpeechRate(s.speechRate)
            }
        }

        viewModelScope.launch {
            observeSettingsUseCase()
                .map { it.requiredCorrectAnswers }
                .distinctUntilChanged()
                .collect { required ->
                    wordRepository.reconcileMastery(required)
                }
        }

        viewModelScope.launch {
            val settingsFlow = observeSettingsUseCase()
            val wordsFlow = wordRepository.observeWordsForList(listId)

            combine(
                wordsFlow,
                settingsFlow.map { it.requiredCorrectAnswers }.distinctUntilChanged()
            ) { words, required ->
                required to words
            }.collect { (required, words) ->
                val unmastered = words.filter { it.correctCount < required || !it.isMastered }
                val currentWordId = _state.value.words.getOrNull(_state.value.currentIndex)?.id
                val nextIndex = currentWordId?.let { id ->
                    unmastered.indexOfFirst { it.id == id }.takeIf { it >= 0 } ?: 0
                } ?: 0
                val nextWord = unmastered.getOrNull(nextIndex)
                _state.update { cur ->
                    cur.copy(
                        words = unmastered,
                        currentIndex = nextIndex.coerceIn(0, (unmastered.size - 1).coerceAtLeast(0)),
                        letters = nextWord?.let { shuffleLetters(it.text) }.orEmpty(),
                        requiredCorrectAnswers = required
                    )
                }
            }
        }
    }

    fun showHints() {
        _state.update { cur ->
            if (!cur.hintsEnabled) cur.copy(showHints = false) else cur.copy(showHints = true)
        }
    }

    fun listen() {
        val w = currentWord() ?: return
        if (_state.value.audioEnabled) {
            tts.speak(w.text)
        }
    }

    fun onInputChange(value: String) {
        _state.update { it.copy(input = value) }
    }

    fun appendLetter(ch: String) {
        _state.update { it.copy(input = it.input + ch) }
    }

    fun clearAnimationHint() {
        _state.update { it.copy(animationHint = PracticeAnimHint.None, wordJustMastered = false) }
    }

    fun checkWord(onFinishedNavigate: () -> Unit) {
        val w = currentWord() ?: run {
            viewModelScope.launch { finishSession(onFinishedNavigate) }
            return
        }
        viewModelScope.launch {
            val settings = observeSettingsUseCase().first()
            val result = processSpelling(
                w,
                _state.value.input,
                settings.requiredCorrectAnswers,
                settings.mistakeBehavior
            )
            val justMastered = !w.isMastered && result.updatedWord.isMastered
            if (result.isSpellingCorrect) {
                val before = rewardRepository.rewardState.first()
                val isFirstEver = before.totalCorrectLifetime == 0
                sessionBadges += rewardRepository.onCorrectAnswer(isFirstEver)
                sound.playSuccess()
                val newSessionCorrect = _state.value.sessionCorrect + 1
                val total = _state.value.words.size

                // Update local snapshot so UI can show per-word progress immediately.
                val updatedWords = _state.value.words.toMutableList().also { list ->
                    val idx = list.indexOfFirst { it.id == result.updatedWord.id }
                    if (idx >= 0) list[idx] = result.updatedWord
                }

                // Move forward; if the word is mastered it will be filtered out by flow, but we still advance in this snapshot.
                val nextIndex = (_state.value.currentIndex + 1).coerceAtMost(updatedWords.size)
                if (nextIndex >= total) {
                    _state.update {
                        it.copy(
                            sessionCorrect = newSessionCorrect,
                            animationHint = if (settings.animationsEnabled) PracticeAnimHint.BounceOk else PracticeAnimHint.None,
                            feedbackCorrect = true,
                            input = "",
                            words = updatedWords,
                            wordJustMastered = justMastered
                        )
                    }
                    finishSession(newSessionCorrect, onFinishedNavigate)
                } else {
                    val nextWord = updatedWords[nextIndex]
                    _state.update {
                        it.copy(
                            sessionCorrect = newSessionCorrect,
                            currentIndex = nextIndex,
                            letters = shuffleLetters(nextWord.text),
                            feedbackCorrect = true,
                            animationHint = if (settings.animationsEnabled) PracticeAnimHint.BounceOk else PracticeAnimHint.None,
                            input = "",
                            showHints = false,
                            words = updatedWords,
                            wordJustMastered = justMastered
                        )
                    }
                }
            } else {
                sound.playRetry()
                _state.update {
                    it.copy(
                        incorrectSubmissions = it.incorrectSubmissions + 1,
                        feedbackCorrect = false,
                        animationHint = if (settings.animationsEnabled) PracticeAnimHint.ShakeWrong else PracticeAnimHint.None,
                        input = "",
                        wordJustMastered = false
                    )
                }
            }
        }
    }

    fun resetListProgress() {
        viewModelScope.launch {
            wordRepository.resetProgress(listId)
        }
    }

    private suspend fun finishSession(finalCorrect: Int, onNavigate: () -> Unit) {
        val s = _state.value
        val total = s.words.size
        if (total == 0) {
            onNavigate()
            return
        }
        val perfect = s.incorrectSubmissions == 0
        sessionBadges += rewardRepository.onSessionCompleted(perfect, total)
        val listName = wordRepository.getWordListName(listId).orEmpty()
        practiceResultCache.set(
            PracticeResult(
                listId = listId,
                listName = listName,
                correct = finalCorrect.coerceAtMost(total),
                total = total,
                starsEarned = finalCorrect,
                newBadges = sessionBadges.distinct(),
                mistakeWordIds = emptyList()
            )
        )
        sound.playCompletion()
        onNavigate()
    }

    private suspend fun finishSession(onNavigate: () -> Unit) {
        finishSession(_state.value.sessionCorrect, onNavigate)
    }

    private fun currentWord(): Word? =
        _state.value.words.getOrNull(_state.value.currentIndex)

    private fun shuffleLetters(text: String): List<String> {
        val lettersOnly = text.filter { it.isLetter() }.map { it.toString() }
        return if (lettersOnly.isEmpty()) {
            text.map { it.toString() }.shuffled()
        } else {
            lettersOnly.shuffled()
        }
    }
}
