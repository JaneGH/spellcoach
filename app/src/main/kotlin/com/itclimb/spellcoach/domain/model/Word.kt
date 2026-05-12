package com.itclimb.spellcoach.domain.model

data class Word(
    val id: Long,
    val listId: Long,
    val text: String,
    val correctCount: Int,
    val incorrectCount: Int,
    val isMastered: Boolean,
    val masteredAt: Long? = null
)

fun Word.hasPersistedMastery(): Boolean = isMastered || masteredAt != null

fun Word.isLearnedAtThreshold(requiredCorrectAnswers: Int): Boolean {
    if (hasPersistedMastery()) return true
    val r = requiredCorrectAnswers.coerceAtLeast(1)
    return correctCount >= r
}
