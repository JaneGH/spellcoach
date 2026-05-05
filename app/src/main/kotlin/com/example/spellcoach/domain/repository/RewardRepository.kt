package com.example.spellcoach.domain.repository

import com.example.spellcoach.domain.model.Badge
import com.example.spellcoach.domain.model.RewardState
import kotlinx.coroutines.flow.Flow

interface RewardRepository {
    val rewardState: Flow<RewardState>
    suspend fun onCorrectAnswer(isFirstLifetimeCorrect: Boolean): List<Badge>
    suspend fun onSessionCompleted(allCorrect: Boolean, totalWords: Int): List<Badge>
}
