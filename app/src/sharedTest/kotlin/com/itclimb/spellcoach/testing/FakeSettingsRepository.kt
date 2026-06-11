package com.itclimb.spellcoach.testing

import com.itclimb.spellcoach.domain.model.AppSettings
import com.itclimb.spellcoach.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSettingsRepository(
    initial: AppSettings = AppSettings()
) : SettingsRepository {
    private val settingsFlow = MutableStateFlow(initial)

    override val settings: Flow<AppSettings> = settingsFlow.asStateFlow()

    fun setSettings(settings: AppSettings) {
        settingsFlow.value = settings
    }

    override suspend fun update(transform: (AppSettings) -> AppSettings) {
        settingsFlow.value = transform(settingsFlow.value)
    }
}
