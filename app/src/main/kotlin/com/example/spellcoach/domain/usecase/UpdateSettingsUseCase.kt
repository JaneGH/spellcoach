package com.example.spellcoach.domain.usecase

import com.example.spellcoach.domain.model.AppSettings
import com.example.spellcoach.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(transform: (AppSettings) -> AppSettings) {
        settingsRepository.update(transform)
    }
}
