package com.itclimb.spellcoach.di

import com.itclimb.spellcoach.data.practice.PracticeResultCache
import com.itclimb.spellcoach.data.sound.SoundEffectPlayer
import com.itclimb.spellcoach.data.tts.TtsManager
import com.itclimb.spellcoach.domain.practice.PracticeResultBuffer
import com.itclimb.spellcoach.domain.speech.RewardSoundPlayer
import com.itclimb.spellcoach.domain.speech.SpellCoachTextToSpeech
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlatformBindingsModule {
    @Binds
    @Singleton
    abstract fun bindPracticeResultBuffer(impl: PracticeResultCache): PracticeResultBuffer

    @Binds
    @Singleton
    abstract fun bindSpellCoachTextToSpeech(impl: TtsManager): SpellCoachTextToSpeech

    @Binds
    @Singleton
    abstract fun bindRewardSoundPlayer(impl: SoundEffectPlayer): RewardSoundPlayer
}
