package com.itclimb.spellcoach.testing

import com.itclimb.spellcoach.domain.model.WordList

object WordListFixtures {
    fun sampleList(
        id: Long = 1L,
        name: String = "Animals",
        totalWords: Int = 5,
        learnedWords: Int = 2
    ) = WordList(
        id = id,
        name = name,
        createdAt = 0L,
        totalWords = totalWords,
        learnedWords = learnedWords,
        chips = listOf("2 to go")
    )
}
