package com.itclimb.spellcoach.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.itclimb.spellcoach.domain.model.AppSettings
import com.itclimb.spellcoach.testing.FakeSettingsRepository
import com.itclimb.spellcoach.testing.FakeWordRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UpdateSettingsUseCaseTest {

    private val settingsRepository = FakeSettingsRepository()
    private val wordRepository = FakeWordRepository()
    private val useCase = UpdateSettingsUseCase(settingsRepository, wordRepository)

    @Test
    fun requiredCorrectAnswersChanged_reconcilesMasteryOnce() = runTest {
        useCase { it.copy(requiredCorrectAnswers = 5) }

        assertThat(settingsRepository.settings.first().requiredCorrectAnswers).isEqualTo(5)
        assertThat(wordRepository.reconcileMasteryCalls).containsExactly(5)
    }

    @Test
    fun requiredCorrectAnswersUnchanged_doesNotReconcileMastery() = runTest {
        settingsRepository.setSettings(AppSettings(requiredCorrectAnswers = 3))

        useCase { it.copy(speechRate = 1.5f) }

        assertThat(wordRepository.reconcileMasteryCalls).isEmpty()
    }

    @Test
    fun requiredCorrectAnswersSetToSameValue_doesNotReconcileMastery() = runTest {
        settingsRepository.setSettings(AppSettings(requiredCorrectAnswers = 3))

        useCase { it.copy(requiredCorrectAnswers = 3) }

        assertThat(wordRepository.reconcileMasteryCalls).isEmpty()
    }
}
