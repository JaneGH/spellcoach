package com.itclimb.spellcoach

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.itclimb.spellcoach.data.tts.TtsManager
import com.itclimb.spellcoach.lifecycle.TtsProcessLifecycleObserver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SpellCoachApplication : Application() {

    @Inject
    lateinit var ttsManager: TtsManager

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(TtsProcessLifecycleObserver(ttsManager))
    }

    override fun onTerminate() {
        ttsManager.shutdown()
        super.onTerminate()
    }
}
