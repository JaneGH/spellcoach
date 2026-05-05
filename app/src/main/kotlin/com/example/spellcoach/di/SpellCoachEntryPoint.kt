package com.example.spellcoach.di

import com.example.spellcoach.presentation.navigation.PracticeListHolder
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SpellCoachEntryPoint {
    fun practiceListHolder(): PracticeListHolder
}
