package com.itclimb.spellcoach.di

import android.content.Context
import androidx.room.Room
import com.itclimb.spellcoach.data.local.dao.SpellCoachDao
import com.itclimb.spellcoach.data.local.db.SpellCoachDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SpellCoachDatabase {
        val builder = Room.databaseBuilder(
            context,
            SpellCoachDatabase::class.java,
            "spellcoach.db"
        )
        return builder.build()
    }

    @Provides
    fun provideDao(db: SpellCoachDatabase): SpellCoachDao = db.spellCoachDao()
}
