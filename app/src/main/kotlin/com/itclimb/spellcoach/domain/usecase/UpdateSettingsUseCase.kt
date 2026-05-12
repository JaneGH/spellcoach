package com.itclimb.spellcoach.domain.usecase

import com.itclimb.spellcoach.domain.model.AppSettings
import com.itclimb.spellcoach.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(transform: (AppSettings) -> AppSettings) {
        settingsRepository.update(transform)
    }
}
