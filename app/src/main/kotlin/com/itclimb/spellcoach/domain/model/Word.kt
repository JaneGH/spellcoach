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

fun isLearnedAtThreshold(
    isMastered: Boolean,
    masteredAt: Long?,
    correctCount: Int,
    requiredCorrectAnswers: Int,
): Boolean {
    val required = requiredCorrectAnswers.coerceAtLeast(1)
    return isMastered || masteredAt != null || correctCount >= required
}

fun Word.isLearnedAtThreshold(requiredCorrectAnswers: Int): Boolean =
    isLearnedAtThreshold(isMastered, masteredAt, correctCount, requiredCorrectAnswers)
