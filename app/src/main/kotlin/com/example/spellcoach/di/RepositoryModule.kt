package com.example.spellcoach.di

import com.example.spellcoach.data.repository.RewardRepositoryImpl
import com.example.spellcoach.data.repository.SettingsRepositoryImpl
import com.example.spellcoach.data.repository.WordRepositoryImpl
import com.example.spellcoach.domain.repository.RewardRepository
import com.example.spellcoach.domain.repository.SettingsRepository
import com.example.spellcoach.domain.repository.WordRepository
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
