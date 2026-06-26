package com.itclimb.spellcoach.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.itclimb.spellcoach.data.local.dao.SpellCoachDao
import com.itclimb.spellcoach.data.local.entity.WordEntity
import com.itclimb.spellcoach.data.local.entity.WordListEntity
import com.itclimb.spellcoach.data.mapper.toDomain
import com.itclimb.spellcoach.domain.model.Word
import com.itclimb.spellcoach.domain.model.WordList
import com.itclimb.spellcoach.domain.repository.DuplicateWordInListException
import com.itclimb.spellcoach.domain.repository.InvalidWordTextException
import com.itclimb.spellcoach.domain.repository.SettingsRepository
import com.itclimb.spellcoach.domain.repository.WordRepository
import com.itclimb.spellcoach.domain.word.WordTextNormalizer
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

@Singleton
class WordRepositoryImpl @Inject constructor(
    private val dao: SpellCoachDao,
    private val settingsRepository: SettingsRepository,
) : WordRepository {
    override fun observeWordLists(): Flow<List<WordList>> =
        combine(
            dao.observeWordListsWithProgress(),
            settingsRepository.settings,
        ) { rows, settings ->
            val required = settings.requiredCorrectAnswers
            rows.map { it.toDomain(required) }
        }

    override fun observeWordsForList(listId: Long): Flow<List<Word>> =
        dao.observeWordsForList(listId).map { list -> list.map { it.toDomain() } }

    override suspend fun createWordListWithWords(name: String, words: List<String>): Long {
        val normalizedWords = WordTextNormalizer.normalizeWords(words)
        require(normalizedWords.isNotEmpty()) { "no_words" }
        val listEntity = WordListEntity(name = name.trim(), createdAt = System.currentTimeMillis())
        val wordEntities = normalizedWords.map { w ->
            WordEntity(
                listId = 0,
                text = w,
                correctCount = 0,
                incorrectCount = 0,
                isMastered = false,
                masteredAt = null
            )
        }
        return guardUniqueWord {
            dao.createListWithWords(listEntity, wordEntities)
        }
    }

    override suspend fun updateWordListWithWords(listId: Long, name: String, words: List<String>) {
        val normalizedName = name.trim()
        val normalizedWords = WordTextNormalizer.normalizeWords(words)
        require(normalizedWords.isNotEmpty()) { "no_words" }

        val existing = dao.getWordsForList(listId)
        val existingByText = existing.associateBy { it.text }
        val keepTexts = normalizedWords.toSet()

        val toDeleteIds = existing.filter { it.text !in keepTexts }.map { it.id }

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

        guardUniqueWord {
            dao.updateListWithWords(listId, normalizedName, toDeleteIds, toInsert)
        }
    }

    override suspend fun updateWord(word: Word) {
        val text = WordTextNormalizer.normalize(word.text)
            ?: throw InvalidWordTextException()
        if (isDuplicateText(listId = word.listId, text = text, excludeWordId = word.id)) {
            throw DuplicateWordInListException()
        }
        guardUniqueWord {
            dao.updateWord(
                WordEntity(
                    id = word.id,
                    listId = word.listId,
                    text = text,
                    correctCount = word.correctCount,
                    incorrectCount = word.incorrectCount,
                    isMastered = word.isMastered,
                    masteredAt = word.masteredAt
                )
            )
        }
    }

    override suspend fun resetProgress(listId: Long) = dao.resetProgress(listId)

    override suspend fun resetWordProgress(wordId: Long) = dao.resetWordProgress(wordId)

    override suspend fun resetAllProgress() = dao.resetAllProgress()

    override suspend fun reconcileMastery(requiredCorrectAnswers: Int) {
        dao.reconcileMastery(requiredCorrectAnswers = requiredCorrectAnswers.coerceAtLeast(1), now = System.currentTimeMillis())
    }

    override suspend fun deleteWordList(listId: Long) = dao.deleteWordList(listId)

    override suspend fun deleteWord(wordId: Long) {
        dao.deleteWordsById(listOf(wordId))
    }

    override suspend fun getWordListName(listId: Long): String? =
        dao.getWordList(listId)?.name

    override suspend fun getWordsForList(listId: Long): List<Word> =
        dao.getWordsForList(listId).map { it.toDomain() }

    override suspend fun getWordById(wordId: Long): Word? =
        dao.getWord(wordId)?.toDomain()

    private suspend fun isDuplicateText(listId: Long, text: String, excludeWordId: Long): Boolean =
        dao.getWordsForList(listId).any { it.id != excludeWordId && it.text == text }

    private inline fun <T> guardUniqueWord(block: () -> T): T =
        try {
            block()
        } catch (_: SQLiteConstraintException) {
            throw DuplicateWordInListException()
        }
}
