package com.example.spellcoach.domain.usecase

import com.example.spellcoach.domain.model.AppSettings
import com.example.spellcoach.domain.repository.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<AppSettings> = settingsRepository.settings
}
