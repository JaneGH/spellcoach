package com.example.spellcoach.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spellcoach.data.tts.TtsAvailability
import com.example.spellcoach.data.tts.TtsManager
import com.example.spellcoach.domain.model.AppSettings
import com.example.spellcoach.domain.model.MistakeBehavior
import com.example.spellcoach.domain.usecase.ObserveSettingsUseCase
import com.example.spellcoach.domain.usecase.UpdateSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeSettingsUseCase: ObserveSettingsUseCase,
    private val updateSettings: UpdateSettingsUseCase,
    private val ttsManager: TtsManager
) : ViewModel() {

    val settings: StateFlow<AppSettings> = observeSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings()
        )

    val ttsAvailability: StateFlow<TtsAvailability> = ttsManager.availability

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

    fun setAudioEnabled(enabled: Boolean) {
        viewModelScope.launch {
            updateSettings { it.copy(audioEnabled = enabled) }
        }
    }

    fun setSpeechRate(rate: Float) {
        viewModelScope.launch {
            updateSettings { it.copy(speechRate = rate) }
            ttsManager.setSpeechRate(rate)
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

    fun openTtsSettings() {
        ttsManager.openSystemTtsSettings()
    }
}
