package com.example.spellcoach.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.spellcoach.data.local.db.WordListWithProgress
import com.example.spellcoach.data.local.entity.WordEntity
import com.example.spellcoach.data.local.entity.WordListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SpellCoachDao {
    @Transaction
    @Query("SELECT * FROM word_lists ORDER BY createdAt DESC")
    fun observeWordListsWithProgress(): Flow<List<WordListWithProgress>>

    @Query("SELECT * FROM words WHERE listId = :listId ORDER BY id ASC")
    fun observeWordsForList(listId: Long): Flow<List<WordEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertWordList(entity: WordListEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertWords(entities: List<WordEntity>)

    @Update
    suspend fun updateWord(entity: WordEntity)

    @Query(
        """
        UPDATE words SET correctCount = 0, incorrectCount = 0, isMastered = 0
        WHERE listId = :listId
        """
    )
    suspend fun resetProgress(listId: Long)

    @Query("DELETE FROM word_lists WHERE id = :listId")
    suspend fun deleteWordList(listId: Long)

    @Query("SELECT * FROM word_lists WHERE id = :listId LIMIT 1")
    suspend fun getWordList(listId: Long): WordListEntity?

    @Query("SELECT * FROM words WHERE id = :wordId LIMIT 1")
    suspend fun getWord(wordId: Long): WordEntity?

    @Query("SELECT COUNT(*) FROM word_lists")
    suspend fun countWordLists(): Int
}
