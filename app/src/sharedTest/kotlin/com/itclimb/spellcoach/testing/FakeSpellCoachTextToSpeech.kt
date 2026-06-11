package com.itclimb.spellcoach.testing

import com.itclimb.spellcoach.domain.speech.SpellCoachTextToSpeech
import com.itclimb.spellcoach.domain.speech.TtsAvailability
import com.itclimb.spellcoach.domain.speech.TtsEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow

class FakeSpellCoachTextToSpeech : SpellCoachTextToSpeech {
    override val availability: StateFlow<TtsAvailability> =
        MutableStateFlow(TtsAvailability.Ready)

    override val events: Flow<TtsEvent> = emptyFlow()

    var lastSpokenText: String? = null
        private set

    override fun speak(text: String) {
        lastSpokenText = text
    }

    override fun stop() = Unit

    override fun setSpeechRate(rate: Float) = Unit

    override fun openSystemTtsSettings() = Unit
}
