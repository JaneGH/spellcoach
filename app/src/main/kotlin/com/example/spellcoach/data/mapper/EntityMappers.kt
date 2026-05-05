package com.example.spellcoach.data.mapper

import com.example.spellcoach.data.local.db.WordListWithProgress
import com.example.spellcoach.data.local.entity.WordEntity
import com.example.spellcoach.domain.model.Word
import com.example.spellcoach.domain.model.WordList

private fun chipsForListName(name: String): List<String> = when (name) {
    "Week 1: Core Vocabulary" -> listOf("Animal Kingdom", "Action Verbs")
    "Space Adventure" -> listOf("Difficulty: Medium")
    else -> emptyList()
}

fun WordEntity.toDomain(): Word = Word(
    id = id,
    listId = listId,
    text = text,
    correctCount = correctCount,
    incorrectCount = incorrectCount,
    isMastered = isMastered
)

fun WordListWithProgress.toDomain(): WordList {
    val chips = chipsForListName(list.name)
    return WordList(
        id = list.id,
        name = list.name,
        createdAt = list.createdAt,
        totalWords = totalWords,
        learnedWords = learnedWords,
        chips = chips
    )
}
