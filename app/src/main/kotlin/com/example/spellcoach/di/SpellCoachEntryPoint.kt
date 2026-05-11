package com.example.spellcoach.di

import com.example.spellcoach.core.navigation.PracticeListHolder
import com.example.spellcoach.domain.usecase.ObserveSettingsUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SpellCoachEntryPoint {
    fun practiceListHolder(): PracticeListHolder

    fun observeSettingsUseCase(): ObserveSettingsUseCase
}
