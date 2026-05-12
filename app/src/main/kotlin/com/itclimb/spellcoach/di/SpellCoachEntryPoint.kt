package com.itclimb.spellcoach.di

import com.itclimb.spellcoach.core.navigation.PracticeListHolder
import com.itclimb.spellcoach.domain.usecase.ObserveSettingsUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SpellCoachEntryPoint {
    fun practiceListHolder(): PracticeListHolder

    fun observeSettingsUseCase(): ObserveSettingsUseCase
}
