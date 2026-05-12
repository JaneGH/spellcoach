package com.itclimb.spellcoach.domain.usecase

import com.itclimb.spellcoach.domain.model.AppSettings
import com.itclimb.spellcoach.domain.repository.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<AppSettings> = settingsRepository.settings
}
