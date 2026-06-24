package com.itclimb.spellcoach.domain.usecase

import com.itclimb.spellcoach.domain.model.AppSettings
import com.itclimb.spellcoach.domain.repository.SettingsRepository
import com.itclimb.spellcoach.domain.repository.WordRepository
import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val wordRepository: WordRepository,
) {
    suspend operator fun invoke(transform: (AppSettings) -> AppSettings) {
        var reconcileRequired: Int? = null
        settingsRepository.update { current ->
            val next = transform(current)
            if (current.requiredCorrectAnswers != next.requiredCorrectAnswers) {
                reconcileRequired = next.requiredCorrectAnswers
            }
            next
        }
        reconcileRequired?.let { wordRepository.reconcileMastery(it) }
    }
}
