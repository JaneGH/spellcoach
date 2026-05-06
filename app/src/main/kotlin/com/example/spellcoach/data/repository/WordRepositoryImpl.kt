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

}
