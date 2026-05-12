package com.itclimb.spellcoach.domain.model

data class PracticeSession(
    val listId: Long,
    val words: List<Word>,
    val currentIndex: Int,
    val correctAnswers: Int,
    val incorrectAnswers: Int,
    val starsEarned: Int,
    val streak: Int
) {
    val currentWord: Word?
        get() = words.getOrNull(currentIndex)

    val totalWords: Int get() = words.size

    val isComplete: Boolean
        get() = words.isEmpty() || currentIndex >= words.size
}
