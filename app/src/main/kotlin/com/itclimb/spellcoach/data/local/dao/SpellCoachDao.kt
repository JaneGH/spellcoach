package com.itclimb.spellcoach.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.itclimb.spellcoach.data.local.db.WordListWithProgress
import com.itclimb.spellcoach.data.local.entity.WordEntity
import com.itclimb.spellcoach.data.local.entity.WordListEntity
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
        UPDATE words SET correctCount = 0, incorrectCount = 0, isMastered = 0, masteredAt = NULL
        WHERE listId = :listId
        """
    )
    suspend fun resetProgress(listId: Long)

    @Query(
        """
        UPDATE words SET correctCount = 0, incorrectCount = 0, isMastered = 0, masteredAt = NULL
        WHERE id = :wordId
        """
    )
    suspend fun resetWordProgress(wordId: Long)

    @Query(
        """
        UPDATE words SET correctCount = 0, incorrectCount = 0, isMastered = 0, masteredAt = NULL
        """
    )
    suspend fun resetAllProgress()

    @Query("DELETE FROM word_lists WHERE id = :listId")
    suspend fun deleteWordList(listId: Long)

    @Query("SELECT * FROM word_lists WHERE id = :listId LIMIT 1")
    suspend fun getWordList(listId: Long): WordListEntity?

    @Query("SELECT * FROM words WHERE id = :wordId LIMIT 1")
    suspend fun getWord(wordId: Long): WordEntity?

    @Query("SELECT * FROM words WHERE listId = :listId ORDER BY id ASC")
    suspend fun getWordsForList(listId: Long): List<WordEntity>

    @Query("UPDATE word_lists SET name = :name WHERE id = :listId")
    suspend fun renameWordList(listId: Long, name: String)

    @Query("DELETE FROM words WHERE id IN (:wordIds)")
    suspend fun deleteWordsById(wordIds: List<Long>)

    /**
     * Applies the new [requiredCorrectAnswers] only to words that are still learning.
     * Rows with persisted mastery ([isMastered] or [masteredAt]) are left unchanged.
     */
    @Query(
        """
        UPDATE words
        SET isMastered =
          CASE WHEN correctCount >= :requiredCorrectAnswers THEN 1 ELSE 0 END,
            masteredAt =
          CASE
            WHEN correctCount >= :requiredCorrectAnswers THEN :now
            ELSE NULL
          END
        WHERE isMastered = 0 AND masteredAt IS NULL
        """
    )
    suspend fun reconcileMastery(requiredCorrectAnswers: Int, now: Long)

    @Query("SELECT COUNT(*) FROM word_lists")
    suspend fun countWordLists(): Int
}
