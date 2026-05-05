package com.example.spellcoach.data.local.db

import androidx.room.Embedded
import androidx.room.Relation
import com.example.spellcoach.data.local.entity.WordEntity
import com.example.spellcoach.data.local.entity.WordListEntity

data class WordListWithProgress(
    @Embedded val list: WordListEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "listId"
    )
    val words: List<WordEntity>
) {
    val totalWords: Int get() = words.size
    val learnedWords: Int get() = words.count { it.isMastered }
}
