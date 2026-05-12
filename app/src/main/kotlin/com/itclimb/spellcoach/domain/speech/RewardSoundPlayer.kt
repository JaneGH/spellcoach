package com.itclimb.spellcoach.domain.speech

interface RewardSoundPlayer {
    suspend fun playSuccess()
    suspend fun playRetry()
    suspend fun playCompletion()
}
