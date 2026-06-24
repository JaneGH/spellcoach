package com.itclimb.spellcoach.data.tts

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.speech.tts.TextToSpeech
import com.itclimb.spellcoach.di.ApplicationScope
import com.itclimb.spellcoach.domain.speech.SpellCoachTextToSpeech
import com.itclimb.spellcoach.domain.speech.TtsAvailability
import com.itclimb.spellcoach.domain.speech.TtsEvent
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
    @param:ApplicationContext private val context: Context,
    @param:ApplicationScope private val applicationScope: CoroutineScope
) : TextToSpeech.OnInitListener, SpellCoachTextToSpeech {
    private val ttsLock = Any()
    private var tts: TextToSpeech? = null
    private var isShutdown = false
    private var engineGeneration = 0
    private var lastCreatedGeneration = -1

    private val _availability =
        MutableStateFlow<TtsAvailability>(TtsAvailability.Checking)
    override val availability: StateFlow<TtsAvailability> = _availability.asStateFlow()

    private val _speechRate = MutableStateFlow(1f)

    private val _events = MutableSharedFlow<TtsEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val events: Flow<TtsEvent> = _events.asSharedFlow()

    init {
        applicationScope.launch {
            initialize()
        }
    }

    internal fun initialize() {
        synchronized(ttsLock) {
            if (tts != null) return
            isShutdown = false
            lastCreatedGeneration = ++engineGeneration
            _availability.value = TtsAvailability.Checking
            tts = TextToSpeech(context, this@TtsManager)
        }
    }

    fun shutdown() {
        synchronized(ttsLock) {
            if (isShutdown && tts == null) return
            isShutdown = true
            engineGeneration++
            val engine = tts
            tts = null
            engine?.stop()
            engine?.shutdown()
            _availability.value = TtsAvailability.Unavailable
        }
    }

    override fun onInit(status: Int) {
        synchronized(ttsLock) {
            if (isShutdown || tts == null || lastCreatedGeneration != engineGeneration) return
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
    }

    override fun speak(text: String) {
        val engine = synchronized(ttsLock) { tts } ?: return
        if (_availability.value != TtsAvailability.Ready) {
            applicationScope.launch { _events.emit(TtsEvent.EngineNotReady) }
            return
        }
        engine.setSpeechRate(_speechRate.value.coerceIn(0.5f, 2f))
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "spellcoach-${System.currentTimeMillis()}")
    }

    override fun stop() {
        synchronized(ttsLock) { tts }?.stop()
    }

    override fun setSpeechRate(rate: Float) {
        val r = rate.coerceIn(0.5f, 2f)
        _speechRate.value = r
        synchronized(ttsLock) { tts }?.setSpeechRate(r)
    }

    override fun openSystemTtsSettings() {
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
}
