package com.example.spellcoach.data.repository

import com.example.spellcoach.data.settings.SettingsDataStore
import com.example.spellcoach.domain.model.AppSettings
import com.example.spellcoach.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: SettingsDataStore
) : SettingsRepository {
    override val settings: Flow<AppSettings> = dataStore.appSettings

    override suspend fun update(transform: (AppSettings) -> AppSettings) {
        dataStore.updateAppSettings(transform)
    }
}
