package com.itclimb.spellcoach.data.repository

import com.itclimb.spellcoach.data.local.dao.SpellCoachDao
import com.itclimb.spellcoach.data.local.entity.WordEntity
import com.itclimb.spellcoach.data.local.entity.WordListEntity
import com.itclimb.spellcoach.data.mapper.toDomain
import com.itclimb.spellcoach.domain.model.Word
import com.itclimb.spellcoach.domain.model.WordList
import com.itclimb.spellcoach.domain.repository.WordRepository
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
                    isMastered = false,
                    masteredAt = null
                )
            }
            dao.insertWords(entities)
        }
        return listId
    }

    override suspend fun updateWordListWithWords(listId: Long, name: String, words: List<String>) {
        val normalizedName = name.trim()
        val normalizedWords = words.map { it.trim() }.filter { it.isNotEmpty() }.distinct()

        val existing = dao.getWordsForList(listId)
        val existingByText = existing.associateBy { it.text.trim() }
        val keepTexts = normalizedWords.toSet()

        val toDeleteIds = existing.filter { it.text.trim() !in keepTexts }.map { it.id }
        if (toDeleteIds.isNotEmpty()) dao.deleteWordsById(toDeleteIds)

        val toInsert = normalizedWords
            .filter { it !in existingByText.keys }
            .map { text ->
                WordEntity(
                    listId = listId,
                    text = text,
                    correctCount = 0,
                    incorrectCount = 0,
                    isMastered = false,
                    masteredAt = null
                )
            }
        if (toInsert.isNotEmpty()) dao.insertWords(toInsert)

        if (normalizedName.isNotEmpty()) {
            dao.renameWordList(listId, normalizedName)
        }
    }

    override suspend fun updateWord(word: Word) {
        dao.updateWord(
            WordEntity(
                id = word.id,
                listId = word.listId,
                text = word.text,
                correctCount = word.correctCount,
                incorrectCount = word.incorrectCount,
                isMastered = word.isMastered,
                masteredAt = word.masteredAt
            )
        )
    }

    override suspend fun resetProgress(listId: Long) = dao.resetProgress(listId)

    override suspend fun resetWordProgress(wordId: Long) = dao.resetWordProgress(wordId)

    override suspend fun resetAllProgress() = dao.resetAllProgress()

    override suspend fun reconcileMastery(requiredCorrectAnswers: Int) {
        dao.reconcileMastery(requiredCorrectAnswers = requiredCorrectAnswers.coerceAtLeast(1), now = System.currentTimeMillis())
    }

    override suspend fun deleteWordList(listId: Long) = dao.deleteWordList(listId)

    override suspend fun getWordListName(listId: Long): String? =
        dao.getWordList(listId)?.name

    override suspend fun getWordsForList(listId: Long): List<Word> =
        dao.getWordsForList(listId).map { it.toDomain() }

    override suspend fun getWordById(wordId: Long): Word? =
        dao.getWord(wordId)?.toDomain()

}
