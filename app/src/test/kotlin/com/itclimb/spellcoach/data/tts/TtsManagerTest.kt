package com.itclimb.spellcoach.data.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

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
}
