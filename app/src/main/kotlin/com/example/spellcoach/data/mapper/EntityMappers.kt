package com.example.spellcoach.data.mapper

import com.example.spellcoach.data.local.db.WordListWithProgress
import com.example.spellcoach.data.local.entity.WordEntity
import com.example.spellcoach.domain.model.Word
import com.example.spellcoach.domain.model.WordList

private fun chipsForListName(name: String): List<String> = when (name) {
    "" -> listOf("")
    else -> emptyList()
}

fun WordEntity.toDomain(): Word = Word(
    id = id,
    listId = listId,
    text = text,
    correctCount = correctCount,
    incorrectCount = incorrectCount,
    isMastered = isMastered,
    masteredAt = masteredAt
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
