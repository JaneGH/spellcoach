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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PracticeUiState(
    val listId: Long = 0L,
    val listName: String = "",
    val words: List<Word> = emptyList(),
    val currentIndex: Int = 0,
    val input: String = "",
    val letters: List<String> = emptyList(),
    val sessionCorrect: Int = 0,
    val incorrectSubmissions: Int = 0,
    val loading: Boolean = true,
    val feedbackCorrect: Boolean? = null,
    val animationHint: PracticeAnimHint = PracticeAnimHint.None,
    val audioEnabled: Boolean = true
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
            val settings = observeSettingsUseCase().first()
            tts.setSpeechRate(settings.speechRate)
            val name = wordRepository.getWordListName(listId).orEmpty()
            val list = wordRepository.observeWordsForList(listId).first()
            val practiceWords = list.filter { !it.isMastered }.ifEmpty { list }
            val letters = practiceWords.firstOrNull()?.let { shuffleLetters(it.text) }.orEmpty()
            _state.update {
                it.copy(
                    listName = name,
                    words = practiceWords,
                    letters = letters,
                    loading = false,
                    audioEnabled = settings.audioEnabled
                )
            }
        }
        viewModelScope.launch {
            observeSettingsUseCase().collect { s ->
                _state.update { it.copy(audioEnabled = s.audioEnabled) }
                tts.setSpeechRate(s.speechRate)
            }
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
        _state.update { it.copy(animationHint = PracticeAnimHint.None) }
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
            if (result.isSpellingCorrect) {
                val before = rewardRepository.rewardState.first()
                val isFirstEver = before.totalCorrectLifetime == 0
                sessionBadges += rewardRepository.onCorrectAnswer(isFirstEver)
                sound.playSuccess()
                val newSessionCorrect = _state.value.sessionCorrect + 1
                val nextIndex = _state.value.currentIndex + 1
                val total = _state.value.words.size
                if (nextIndex >= total) {
                    _state.update {
                        it.copy(
                            sessionCorrect = newSessionCorrect,
                            animationHint = if (settings.animationsEnabled) PracticeAnimHint.BounceOk else PracticeAnimHint.None,
                            feedbackCorrect = true,
                            input = ""
                        )
                    }
                    finishSession(newSessionCorrect, onFinishedNavigate)
                } else {
                    val nextWord = _state.value.words[nextIndex]
                    _state.update {
                        it.copy(
                            sessionCorrect = newSessionCorrect,
                            currentIndex = nextIndex,
                            letters = shuffleLetters(nextWord.text),
                            feedbackCorrect = true,
                            animationHint = if (settings.animationsEnabled) PracticeAnimHint.BounceOk else PracticeAnimHint.None,
                            input = ""
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
                        input = ""
                    )
                }
            }
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
