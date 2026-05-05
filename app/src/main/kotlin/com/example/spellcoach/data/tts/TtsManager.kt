package com.example.spellcoach.data.tts

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.speech.tts.TextToSpeech
import com.example.spellcoach.di.ApplicationScope
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Singleton
class TtsManager @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationScope private val applicationScope: CoroutineScope
) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private val _availability =
        MutableStateFlow<TtsAvailability>(TtsAvailability.Checking)
    val availability: StateFlow<TtsAvailability> = _availability.asStateFlow()

    private val _speechRate = MutableStateFlow(1f)

    private val _events = MutableSharedFlow<TtsEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: Flow<TtsEvent> = _events.asSharedFlow()

    init {
        applicationScope.launch {
            tts = TextToSpeech(context, this@TtsManager)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US) ?: TextToSpeech.LANG_NOT_SUPPORTED
            _availability.value = if (
                result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                TtsAvailability.MissingData
            } else {
                TtsAvailability.Ready
            }
        } else {
            _availability.value = TtsAvailability.Unavailable
        }
    }

    fun speak(text: String) {
        val engine = tts ?: return
        if (_availability.value != TtsAvailability.Ready) {
            applicationScope.launch { _events.emit(TtsEvent.EngineNotReady) }
            return
        }
        engine.setSpeechRate(_speechRate.value.coerceIn(0.5f, 2f))
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "spellcoach-${System.currentTimeMillis()}")
    }

    fun stop() {
        tts?.stop()
    }

    fun setSpeechRate(rate: Float) {
        val r = rate.coerceIn(0.5f, 2f)
        _speechRate.value = r
        tts?.setSpeechRate(r)
    }

    fun openSystemTtsSettings() {
        val ttsIntent = Intent("com.android.settings.TTS_SETTINGS").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (runCatching { context.startActivity(ttsIntent) }.isFailure) {
            val install = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            if (runCatching { context.startActivity(install) }.isFailure) {
                val fallback = Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                runCatching { context.startActivity(fallback) }
            }
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }

}

sealed interface TtsAvailability {
    data object Checking : TtsAvailability
    data object Ready : TtsAvailability
    data object MissingData : TtsAvailability
    data object Unavailable : TtsAvailability
}

sealed interface TtsEvent {
    data object EngineNotReady : TtsEvent
}
