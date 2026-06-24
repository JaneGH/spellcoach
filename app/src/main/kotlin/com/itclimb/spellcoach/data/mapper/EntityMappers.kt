package com.itclimb.spellcoach.data.mapper

import com.itclimb.spellcoach.data.local.db.WordListWithProgress
import com.itclimb.spellcoach.data.local.entity.WordEntity
import com.itclimb.spellcoach.domain.model.Word
import com.itclimb.spellcoach.domain.model.WordList
import com.itclimb.spellcoach.domain.model.isLearnedAtThreshold

private fun chipsForListName(name: String): List<String> = when (name) {
    "" -> listOf("")
    else -> emptyList()
}

fun WordEntity.isLearned(requiredCorrectAnswers: Int): Boolean =
    isLearnedAtThreshold(isMastered, masteredAt, correctCount, requiredCorrectAnswers)

fun WordEntity.toDomain(): Word = Word(
    id = id,
    listId = listId,
    text = text,
    correctCount = correctCount,
    incorrectCount = incorrectCount,
    isMastered = isMastered,
    masteredAt = masteredAt
)

fun WordListWithProgress.toDomain(requiredCorrectAnswers: Int): WordList {
    val chips = chipsForListName(list.name)
    return WordList(
        id = list.id,
        name = list.name,
        createdAt = list.createdAt,
        totalWords = totalWords,
        learnedWords = words.count { it.isLearned(requiredCorrectAnswers) },
        chips = chips
    )
}
