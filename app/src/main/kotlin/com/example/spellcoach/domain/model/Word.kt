package com.example.spellcoach.domain.model

data class Word(
    val id: Long,
    val listId: Long,
    val text: String,
    val correctCount: Int,
    val incorrectCount: Int,
    val isMastered: Boolean,
    val masteredAt: Long? = null
)
