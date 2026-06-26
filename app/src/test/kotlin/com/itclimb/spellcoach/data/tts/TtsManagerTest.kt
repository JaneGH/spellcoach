package com.itclimb.spellcoach.data.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import com.google.common.truth.Truth.assertThat
import com.itclimb.spellcoach.domain.speech.TtsAvailability
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class TtsManagerTest {

    @Test
    fun shutdown_callsTextToSpeechShutdownExactlyOnce() = runTest {
        val manager = createManagerWithoutInit()
        val mockTts = mockk<TextToSpeech>(relaxed = true)
        manager.setTtsForTest(mockTts)

        manager.shutdown()

        verify(exactly = 1) { mockTts.stop() }
        verify(exactly = 1) { mockTts.shutdown() }
    }

    @Test
    fun repeatedShutdown_isSafe() = runTest {
        val manager = createManagerWithoutInit()
        val mockTts = mockk<TextToSpeech>(relaxed = true)
        manager.setTtsForTest(mockTts)

        manager.shutdown()
        manager.shutdown()

        verify(exactly = 1) { mockTts.shutdown() }
    }

    @Test
    fun shutdown_clearsTtsReference() = runTest {
        val manager = createManagerWithoutInit()
        manager.setTtsForTest(mockk<TextToSpeech>(relaxed = true))

        manager.shutdown()

        assertThat(manager.ttsForTest()).isNull()
    }

    @Test
    fun shutdown_withoutInitializedEngine_isSafe() = runTest {
        val manager = createManagerWithoutInit()

        manager.shutdown()
        manager.shutdown()

        assertThat(manager.ttsForTest()).isNull()
    }

    @Test
    fun speak_whileChecking_queuesLatestText() = runTest {
        val manager = createManagerWithoutInit()
        manager.setTtsForTest(mockk<TextToSpeech>(relaxed = true))
        manager.setAvailabilityForTest(TtsAvailability.Checking)

        manager.speak("first")
        manager.speak("second")

        assertThat(manager.pendingSpeakForTest()).isEqualTo("second")
        verify(exactly = 0) { manager.ttsForTest()!!.speak(any(), any(), any(), any()) }
    }

    @Test
    fun speak_beforeOnInit_playsOnceAfterInit() = runTest {
        val manager = createManagerWithoutInit()
        val mockTts = mockk<TextToSpeech>(relaxed = true)
        every { mockTts.setLanguage(Locale.US) } returns TextToSpeech.LANG_AVAILABLE
        manager.prepareEngineForInitTest(mockTts)
        manager.setAvailabilityForTest(TtsAvailability.Checking)

        manager.speak("hello")
        manager.onInit(TextToSpeech.SUCCESS)

        assertThat(manager.pendingSpeakForTest()).isNull()
        assertThat(manager.availability.value).isEqualTo(TtsAvailability.Ready)
        verify(exactly = 1) {
            mockTts.speak("hello", TextToSpeech.QUEUE_FLUSH, null, any())
        }
    }

    @Test
    fun onInit_success_flushesPendingSpeak() = runTest {
        val manager = createManagerWithoutInit()
        val mockTts = mockk<TextToSpeech>(relaxed = true)
        every { mockTts.setLanguage(Locale.US) } returns TextToSpeech.LANG_AVAILABLE
        manager.prepareEngineForInitTest(mockTts)
        manager.setAvailabilityForTest(TtsAvailability.Checking)
        manager.setPendingSpeakForTest("hello")

        manager.onInit(TextToSpeech.SUCCESS)

        assertThat(manager.pendingSpeakForTest()).isNull()
        assertThat(manager.availability.value).isEqualTo(TtsAvailability.Ready)
        verify(exactly = 1) {
            mockTts.speak("hello", TextToSpeech.QUEUE_FLUSH, null, any())
        }
    }

    @Test
    fun onInit_failure_clearsPendingWithoutSpeaking() = runTest {
        val manager = createManagerWithoutInit()
        val mockTts = mockk<TextToSpeech>(relaxed = true)
        manager.prepareEngineForInitTest(mockTts)
        manager.setPendingSpeakForTest("hello")

        manager.onInit(TextToSpeech.ERROR)

        assertThat(manager.pendingSpeakForTest()).isNull()
        verify(exactly = 0) { mockTts.speak(any(), any(), any(), any()) }
    }

    @Test
    fun stop_clearsPendingSpeak() = runTest {
        val manager = createManagerWithoutInit()
        val mockTts = mockk<TextToSpeech>(relaxed = true)
        manager.setTtsForTest(mockTts)
        manager.setAvailabilityForTest(TtsAvailability.Checking)
        manager.speak("hello")

        manager.stop()

        assertThat(manager.pendingSpeakForTest()).isNull()
        verify(exactly = 1) { mockTts.stop() }
    }

    private fun createManagerWithoutInit(): TtsManager {
        val dispatcher = StandardTestDispatcher()
        val scope = CoroutineScope(SupervisorJob() + dispatcher)
        val context = mockk<Context>(relaxed = true)
        return TtsManager(context, scope)
    }

    private fun TtsManager.setTtsForTest(engine: TextToSpeech?) {
        val field = TtsManager::class.java.getDeclaredField("tts")
        field.isAccessible = true
        field.set(this, engine)
    }

    private fun TtsManager.ttsForTest(): TextToSpeech? {
        val field = TtsManager::class.java.getDeclaredField("tts")
        field.isAccessible = true
        return field.get(this) as TextToSpeech?
    }

    private fun TtsManager.setAvailabilityForTest(value: TtsAvailability) {
        val field = TtsManager::class.java.getDeclaredField("_availability")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        (field.get(this) as kotlinx.coroutines.flow.MutableStateFlow<TtsAvailability>).value = value
    }

    private fun TtsManager.pendingSpeakForTest(): String? {
        val field = TtsManager::class.java.getDeclaredField("pendingSpeakText")
        field.isAccessible = true
        return field.get(this) as String?
    }

    private fun TtsManager.setPendingSpeakForTest(value: String?) {
        val field = TtsManager::class.java.getDeclaredField("pendingSpeakText")
        field.isAccessible = true
        field.set(this, value)
    }

    private fun TtsManager.prepareEngineForInitTest(engine: TextToSpeech) {
        setTtsForTest(engine)
        setIntField("engineGeneration", 1)
        setIntField("lastCreatedGeneration", 1)
        setBooleanField("isShutdown", false)
    }

    private fun TtsManager.setIntField(name: String, value: Int) {
        val field = TtsManager::class.java.getDeclaredField(name)
        field.isAccessible = true
        field.setInt(this, value)
    }

    private fun TtsManager.setBooleanField(name: String, value: Boolean) {
        val field = TtsManager::class.java.getDeclaredField(name)
        field.isAccessible = true
        field.setBoolean(this, value)
    }
}
