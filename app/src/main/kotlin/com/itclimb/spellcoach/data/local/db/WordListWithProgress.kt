package com.itclimb.spellcoach.data.local.db

import androidx.room.Embedded
import androidx.room.Relation
import com.itclimb.spellcoach.data.local.entity.WordEntity
import com.itclimb.spellcoach.data.local.entity.WordListEntity

data class WordListWithProgress(
    @Embedded val list: WordListEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "listId"
    )
    val words: List<WordEntity>
) {
    val totalWords: Int get() = words.size
}
