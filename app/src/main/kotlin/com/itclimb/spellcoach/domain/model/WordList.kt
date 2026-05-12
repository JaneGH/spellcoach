package com.itclimb.spellcoach.domain.model

data class WordList(
    val id: Long,
    val name: String,
    val createdAt: Long,
    val totalWords: Int,
    val learnedWords: Int,
    val chips: List<String> = emptyList()
) {
    val progress: Float
        get() = if (totalWords == 0) 0f else learnedWords.toFloat() / totalWords.toFloat()

    val isMastered: Boolean
        get() = totalWords > 0 && learnedWords >= totalWords
}
