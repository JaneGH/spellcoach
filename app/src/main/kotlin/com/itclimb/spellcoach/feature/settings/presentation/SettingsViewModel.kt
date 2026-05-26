package com.itclimb.spellcoach.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itclimb.spellcoach.domain.speech.SpellCoachTextToSpeech
import com.itclimb.spellcoach.domain.speech.TtsAvailability
import com.itclimb.spellcoach.domain.model.AppSettings
import com.itclimb.spellcoach.domain.model.MistakeBehavior
import com.itclimb.spellcoach.domain.model.ThemePreference
import com.itclimb.spellcoach.domain.usecase.ObserveSettingsUseCase
import com.itclimb.spellcoach.domain.usecase.UpdateSettingsUseCase
import com.itclimb.spellcoach.domain.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeSettingsUseCase: ObserveSettingsUseCase,
    private val updateSettings: UpdateSettingsUseCase,
    private val wordRepository: WordRepository,
    private val textToSpeech: SpellCoachTextToSpeech
) : ViewModel() {

    val settings: StateFlow<AppSettings> = observeSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings()
        )

    val ttsAvailability: StateFlow<TtsAvailability> = textToSpeech.availability

    init {
        viewModelScope.launch {
            settings
                .map { it.requiredCorrectAnswers }
                .distinctUntilChanged()
                .collect { required ->
                    wordRepository.reconcileMastery(required)
                }
        }
    }

    fun setRequiredCorrect(n: Int) {
        viewModelScope.launch {
            updateSettings { it.copy(requiredCorrectAnswers = n.coerceIn(1, 10)) }
        }
    }

    fun setMistakeBehavior(behavior: MistakeBehavior) {
        viewModelScope.launch {
            updateSettings { it.copy(mistakeBehavior = behavior) }
        }
    }

    fun setAnswerSoundsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            updateSettings { it.copy(answerSoundsEnabled = enabled) }
        }
    }

    fun setLetterHintsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            updateSettings { it.copy(letterHintsEnabled = enabled) }
        }
    }

    fun setExcludeMasteredWordsFromPractice(enabled: Boolean) {
        viewModelScope.launch {
            updateSettings { it.copy(excludeMasteredWordsFromPractice = enabled) }
        }
    }

    fun setSpeechRate(rate: Float) {
        viewModelScope.launch {
            updateSettings { it.copy(speechRate = rate) }
            textToSpeech.setSpeechRate(rate)
        }
    }

    fun setRewardSounds(enabled: Boolean) {
        viewModelScope.launch {
            updateSettings { it.copy(rewardSoundsEnabled = enabled) }
        }
    }

    fun setAnimations(enabled: Boolean) {
        viewModelScope.launch {
            updateSettings { it.copy(animationsEnabled = enabled) }
        }
    }

    fun setThemePreference(preference: ThemePreference) {
        viewModelScope.launch {
            updateSettings { it.copy(themePreference = preference) }
        }
    }

    fun openTtsSettings() {
        textToSpeech.openSystemTtsSettings()
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            wordRepository.resetAllProgress()
        }
    }
}
