package com.itclimb.spellcoach.data.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.itclimb.spellcoach.R
import com.itclimb.spellcoach.data.settings.SettingsDataStore
import com.itclimb.spellcoach.domain.speech.RewardSoundPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class SoundEffectPlayer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsDataStore: SettingsDataStore
) : RewardSoundPlayer {
    private val pool: SoundPool = SoundPool.Builder()
        .setMaxStreams(3)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private var successId: Int = 0
    private var retryId: Int = 0
    private var completionId: Int = 0

    init {
        successId = pool.load(context, R.raw.success, 1)
        retryId = pool.load(context, R.raw.retry, 1)
        completionId = pool.load(context, R.raw.completion, 1)
    }

    override suspend fun playSuccess() = playIfEnabled(successId)

    override suspend fun playRetry() = playIfEnabled(retryId)

    override suspend fun playCompletion() = playIfEnabled(completionId)

    private suspend fun playIfEnabled(soundId: Int) {
        if (soundId == 0) return
        val enabled = settingsDataStore.appSettings.first().rewardSoundsEnabled
        if (enabled) {
            pool.play(soundId, 1f, 1f, 1, 0, 1f)
        }
    }
}
