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

    /** Latest utterance requested while the engine is still [TtsAvailability.Checking]. */
    private var pendingSpeakText: String? = null

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
            pendingSpeakText = null
            val engine = tts
            tts = null
            engine?.stop()
            engine?.shutdown()
            _availability.value = TtsAvailability.Unavailable
        }
    }

    override fun onInit(status: Int) {
        var flushRequest: Pair<TextToSpeech, String>? = null
        var emitNotReady = false

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

            when (_availability.value) {
                TtsAvailability.Ready -> {
                    val pending = pendingSpeakText
                    pendingSpeakText = null
                    val engine = tts
                    if (pending != null && engine != null) {
                        flushRequest = engine to pending
                    }
                }
                else -> {
                    emitNotReady = pendingSpeakText != null
                    pendingSpeakText = null
                }
            }
        }

        flushRequest?.let { (engine, pending) -> speakNow(engine, pending) }
        if (emitNotReady) {
            applicationScope.launch { _events.emit(TtsEvent.EngineNotReady) }
        }
    }

    override fun speak(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        var speakImmediately: Pair<TextToSpeech, String>? = null
        var emitNotReady = false

        synchronized(ttsLock) {
            when (_availability.value) {
                TtsAvailability.Ready -> {
                    val engine = tts
                    if (engine != null) {
                        speakImmediately = engine to trimmed
                    } else {
                        pendingSpeakText = trimmed
                    }
                }
                TtsAvailability.Checking -> pendingSpeakText = trimmed
                TtsAvailability.MissingData, TtsAvailability.Unavailable -> emitNotReady = true
            }
        }

        speakImmediately?.let { (engine, utterance) -> speakNow(engine, utterance) }
        if (emitNotReady) {
            applicationScope.launch { _events.emit(TtsEvent.EngineNotReady) }
        }
    }

    override fun stop() {
        synchronized(ttsLock) {
            pendingSpeakText = null
            tts?.stop()
        }
    }

    private fun speakNow(engine: TextToSpeech, text: String) {
        engine.setSpeechRate(_speechRate.value.coerceIn(0.5f, 2f))
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "spellcoach-${System.currentTimeMillis()}")
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
