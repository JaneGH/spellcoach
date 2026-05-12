package com.itclimb.spellcoach.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.itclimb.spellcoach.data.local.dao.SpellCoachDao
import com.itclimb.spellcoach.data.local.entity.WordEntity
import com.itclimb.spellcoach.data.local.entity.WordListEntity

@Database(
    entities = [WordListEntity::class, WordEntity::class],
    version = 2,
    exportSchema = false
)
abstract class SpellCoachDatabase : RoomDatabase() {
    abstract fun spellCoachDao(): SpellCoachDao
}
