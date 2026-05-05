package com.example.spellcoach.domain.model

data class RewardState(
    val totalCorrectLifetime: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val lastPracticeDayEpochDay: Long,
    val unlockedBadges: Set<Badge>
)
