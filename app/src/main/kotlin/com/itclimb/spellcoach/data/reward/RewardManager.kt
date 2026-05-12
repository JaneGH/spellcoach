package com.itclimb.spellcoach.data.reward

import com.itclimb.spellcoach.data.settings.SettingsDataStore
import com.itclimb.spellcoach.domain.model.Badge
import com.itclimb.spellcoach.domain.model.RewardState
import com.itclimb.spellcoach.domain.reward.RewardBadgeRules
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class RewardManager @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) {
    suspend fun onCorrectAnswer(isFirstLifetimeCorrect: Boolean): List<Badge> {
        val collected = mutableListOf<Badge>()
        settingsDataStore.updateRewardState { state ->
            val transition = RewardBadgeRules.onCorrectAnswer(state, isFirstLifetimeCorrect)
            collected += transition.newBadges
            transition.nextState
        }
        return collected
    }

    suspend fun onSessionCompleted(allCorrect: Boolean, totalWords: Int): List<Badge> {
        val collected = mutableListOf<Badge>()
        settingsDataStore.updateRewardState { state ->
            val transition = RewardBadgeRules.onSessionCompleted(state, allCorrect, totalWords)
            collected += transition.newBadges
            transition.nextState
        }
        return collected
    }

    suspend fun getState(): RewardState = settingsDataStore.rewardState.first()
}
