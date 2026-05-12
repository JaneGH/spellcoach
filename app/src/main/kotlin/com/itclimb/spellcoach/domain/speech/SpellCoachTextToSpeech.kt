package com.itclimb.spellcoach.domain.speech

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

sealed interface TtsAvailability {
    data object Checking : TtsAvailability
    data object Ready : TtsAvailability
    data object MissingData : TtsAvailability
    data object Unavailable : TtsAvailability
}

sealed interface TtsEvent {
    data object EngineNotReady : TtsEvent
}

interface SpellCoachTextToSpeech {
    val availability: StateFlow<TtsAvailability>
    val events: Flow<TtsEvent>
    fun speak(text: String)
    fun stop()
    fun setSpeechRate(rate: Float)
    fun openSystemTtsSettings()
}
