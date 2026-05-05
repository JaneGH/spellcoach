package com.example.spellcoach.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.spellcoach.data.local.dao.SpellCoachDao
import com.example.spellcoach.data.local.entity.WordEntity
import com.example.spellcoach.data.local.entity.WordListEntity

@Database(
    entities = [WordListEntity::class, WordEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SpellCoachDatabase : RoomDatabase() {
    abstract fun spellCoachDao(): SpellCoachDao
}
