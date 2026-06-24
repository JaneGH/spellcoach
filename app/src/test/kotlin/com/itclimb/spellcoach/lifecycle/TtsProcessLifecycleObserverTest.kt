package com.itclimb.spellcoach.lifecycle

import androidx.lifecycle.LifecycleOwner
import com.itclimb.spellcoach.data.tts.TtsManager
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class TtsProcessLifecycleObserverTest {

    @Test
    fun onStop_callsShutdown() {
        val ttsManager = mockk<TtsManager>(relaxed = true)
        val observer = TtsProcessLifecycleObserver(ttsManager)
        val owner = mockk<LifecycleOwner>(relaxed = true)

        observer.onStop(owner)

        verify(exactly = 1) { ttsManager.shutdown() }
    }

    @Test
    fun onStart_callsInitialize() {
        val ttsManager = mockk<TtsManager>(relaxed = true)
        val observer = TtsProcessLifecycleObserver(ttsManager)
        val owner = mockk<LifecycleOwner>(relaxed = true)

        observer.onStart(owner)

        verify(exactly = 1) { ttsManager.initialize() }
    }
}
