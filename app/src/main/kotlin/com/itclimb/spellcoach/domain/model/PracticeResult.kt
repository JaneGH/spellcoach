package com.itclimb.spellcoach.domain.model

data class PracticeResult(
    val listId: Long,
    val listName: String,
    val correct: Int,
    val total: Int,
    val starsEarned: Int,
    val newBadges: List<Badge>,
    val mistakeWordIds: List<Long> = emptyList()
)
