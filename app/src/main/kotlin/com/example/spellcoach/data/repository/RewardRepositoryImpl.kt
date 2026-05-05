package com.example.spellcoach.data.repository

import com.example.spellcoach.data.reward.RewardManager
import com.example.spellcoach.data.settings.SettingsDataStore
import com.example.spellcoach.domain.model.Badge
import com.example.spellcoach.domain.model.RewardState
import com.example.spellcoach.domain.repository.RewardRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class RewardRepositoryImpl @Inject constructor(
    private val rewardManager: RewardManager,
    private val settingsDataStore: SettingsDataStore
) : RewardRepository {
    override val rewardState: Flow<RewardState> = settingsDataStore.rewardState

    override suspend fun onCorrectAnswer(isFirstLifetimeCorrect: Boolean): List<Badge> =
        rewardManager.onCorrectAnswer(isFirstLifetimeCorrect)

    override suspend fun onSessionCompleted(allCorrect: Boolean, totalWords: Int): List<Badge> =
        rewardManager.onSessionCompleted(allCorrect, totalWords)
}
