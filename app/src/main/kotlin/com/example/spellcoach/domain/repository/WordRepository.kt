package com.example.spellcoach.domain.repository

import com.example.spellcoach.domain.model.Word
import com.example.spellcoach.domain.model.WordList
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    fun observeWordLists(): Flow<List<WordList>>
    fun observeWordsForList(listId: Long): Flow<List<Word>>
    suspend fun createWordListWithWords(name: String, words: List<String>): Long
    suspend fun updateWord(word: Word)
    suspend fun resetProgress(listId: Long)
    suspend fun deleteWordList(listId: Long)
    suspend fun getWordListName(listId: Long): String?
    suspend fun getWordById(wordId: Long): Word?
}
