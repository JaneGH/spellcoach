package com.itclimb.spellcoach.di

import com.itclimb.spellcoach.data.repository.RewardRepositoryImpl
import com.itclimb.spellcoach.data.repository.SettingsRepositoryImpl
import com.itclimb.spellcoach.data.repository.WordRepositoryImpl
import com.itclimb.spellcoach.domain.repository.RewardRepository
import com.itclimb.spellcoach.domain.repository.SettingsRepository
import com.itclimb.spellcoach.domain.repository.WordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindWordRepository(impl: WordRepositoryImpl): WordRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindRewardRepository(impl: RewardRepositoryImpl): RewardRepository
}
