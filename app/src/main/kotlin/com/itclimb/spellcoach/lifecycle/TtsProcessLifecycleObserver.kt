package com.itclimb.spellcoach.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.itclimb.spellcoach.data.tts.TtsManager

internal class TtsProcessLifecycleObserver(
    private val ttsManager: TtsManager
) : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        ttsManager.initialize()
    }

    override fun onStop(owner: LifecycleOwner) {
        ttsManager.shutdown()
    }
}
