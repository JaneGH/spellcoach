package com.example.spellcoach.data.repository

import com.example.spellcoach.data.local.dao.SpellCoachDao
import com.example.spellcoach.data.local.entity.WordEntity
import com.example.spellcoach.data.local.entity.WordListEntity
import com.example.spellcoach.data.mapper.toDomain
import com.example.spellcoach.domain.model.Word
import com.example.spellcoach.domain.model.WordList
import com.example.spellcoach.domain.repository.WordRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class WordRepositoryImpl @Inject constructor(
    private val dao: SpellCoachDao
) : WordRepository {
    override fun observeWordLists(): Flow<List<WordList>> =
        dao.observeWordListsWithProgress().map { rows -> rows.map { it.toDomain() } }

    override fun observeWordsForList(listId: Long): Flow<List<Word>> =
        dao.observeWordsForList(listId).map { list -> list.map { it.toDomain() } }

    override suspend fun createWordListWithWords(name: String, words: List<String>): Long {
        val listId = dao.insertWordList(WordListEntity(name = name.trim(), createdAt = System.currentTimeMillis()))
        if (words.isNotEmpty()) {
            val entities = words.map { w ->
                WordEntity(
                    listId = listId,
                    text = w.trim(),
                    correctCount = 0,
                    incorrectCount = 0,
                    isMastered = false
                )
            }
            dao.insertWords(entities)
        }
        return listId
    }

    override suspend fun updateWord(word: Word) {
        dao.updateWord(
            WordEntity(
                id = word.id,
                listId = word.listId,
                text = word.text,
                correctCount = word.correctCount,
                incorrectCount = word.incorrectCount,
                isMastered = word.isMastered
            )
        )
    }

    override suspend fun resetProgress(listId: Long) = dao.resetProgress(listId)

    override suspend fun deleteWordList(listId: Long) = dao.deleteWordList(listId)

    override suspend fun getWordListName(listId: Long): String? =
        dao.getWordList(listId)?.name

    override suspend fun getWordById(wordId: Long): Word? =
        dao.getWord(wordId)?.toDomain()

    override suspend fun seedSampleDataIfEmpty() {
        if (dao.countWordLists() > 0) return
        val now = System.currentTimeMillis()
        val w1 = dao.insertWordList(WordListEntity(name = "Week 1: Core Vocabulary", createdAt = now))
        val w2 = dao.insertWordList(WordListEntity(name = "Common Nouns", createdAt = now - 1))
        val w3 = dao.insertWordList(WordListEntity(name = "Space Adventure", createdAt = now - 2))
        insertWeek1Words(w1)
        insertCommonNouns(w2)
        insertSpaceAdventure(w3)
    }

    private suspend fun insertWeek1Words(listId: Long) {
        val words = (1..20).map { i ->
            val mastered = i <= 12
            WordEntity(
                listId = listId,
                text = "word$i",
                correctCount = if (mastered) 3 else 0,
                incorrectCount = 0,
                isMastered = mastered
            )
        }
        dao.insertWords(words)
    }

    private suspend fun insertCommonNouns(listId: Long) {
        val words = (1..18).map {
            WordEntity(
                listId = listId,
                text = "noun$it",
                correctCount = 3,
                incorrectCount = 0,
                isMastered = true
            )
        }
        dao.insertWords(words)
    }

    private suspend fun insertSpaceAdventure(listId: Long) {
        val words = (1..15).map { i ->
            val mastered = i <= 5
            WordEntity(
                listId = listId,
                text = "space$i",
                correctCount = if (mastered) 3 else 1,
                incorrectCount = 0,
                isMastered = mastered
            )
        }
        dao.insertWords(words)
    }
}
