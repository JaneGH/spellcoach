package com.example.spellcoach.domain.model

data class AppSettings(
    val requiredCorrectAnswers: Int = 3,
    val mistakeBehavior: MistakeBehavior = MistakeBehavior.DECREASE_PROGRESS,
    val audioEnabled: Boolean = true,
    val speechRate: Float = 1f,
    val rewardSoundsEnabled: Boolean = true,
    val animationsEnabled: Boolean = true
)
