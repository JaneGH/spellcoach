package com.itclimb.spellcoach.data.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.itclimb.spellcoach.R
import com.itclimb.spellcoach.di.ApplicationScope
import com.itclimb.spellcoach.domain.repository.SettingsRepository
import com.itclimb.spellcoach.domain.speech.RewardSoundPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Singleton
class SoundEffectPlayer @Inject constructor(
    @ApplicationContext private val context: Context,
    settingsRepository: SettingsRepository,
    @ApplicationScope private val applicationScope: CoroutineScope,
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

    @Volatile
    private var answerSoundsEnabled: Boolean = true

    @Volatile
    private var rewardSoundsEnabled: Boolean = true

    init {
        settingsRepository.settings
            .onEach { settings ->
                answerSoundsEnabled = settings.answerSoundsEnabled
                rewardSoundsEnabled = settings.rewardSoundsEnabled
            }
            .launchIn(applicationScope)

        successId = pool.load(context, R.raw.success, 1)
        retryId = pool.load(context, R.raw.retry, 1)
        completionId = pool.load(context, R.raw.completion, 1)
    }

    override suspend fun playSuccess() = playAnswerSoundIfEnabled(successId)

    override suspend fun playRetry() = playAnswerSoundIfEnabled(retryId)

    override suspend fun playCompletion() = playCompletionIfEnabled(completionId)

    private fun playAnswerSoundIfEnabled(soundId: Int) {
        if (soundId == 0 || !answerSoundsEnabled) return
        pool.play(soundId, 1f, 1f, 1, 0, 1f)
    }

    private fun playCompletionIfEnabled(soundId: Int) {
        if (soundId == 0 || !rewardSoundsEnabled) return
        pool.play(soundId, 1f, 1f, 1, 0, 1f)
    }
}
