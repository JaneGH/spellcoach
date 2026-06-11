package com.itclimb.spellcoach.testing

import com.itclimb.spellcoach.domain.model.Word
import com.itclimb.spellcoach.domain.model.WordList
import com.itclimb.spellcoach.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeWordRepository(
    initialLists: List<WordList> = emptyList()
) : WordRepository {

    private val lists = MutableStateFlow(initialLists)
    private val wordsByListId = mutableMapOf<Long, MutableStateFlow<List<Word>>>()

    val resetProgressCalls = mutableListOf<Long>()
    val deleteListCalls = mutableListOf<Long>()
    val deleteWordCalls = mutableListOf<Long>()
    val resetAllProgressCalls = mutableListOf<Unit>()
    val reconcileMasteryCalls = mutableListOf<Int>()
    var lastUpdatedWord: Word? = null
        private set

    fun setLists(newLists: List<WordList>) {
        lists.value = newLists
    }

    fun setWordsForList(listId: Long, words: List<Word>) {
        wordsByListId.getOrPut(listId) { MutableStateFlow(emptyList()) }.value = words
    }

    override fun observeWordLists(): Flow<List<WordList>> = lists.asStateFlow()

    override fun observeWordsForList(listId: Long): Flow<List<Word>> =
        wordsByListId.getOrPut(listId) { MutableStateFlow(emptyList()) }.asStateFlow()

    override suspend fun createWordListWithWords(name: String, words: List<String>): Long {
        error("Not implemented in fake")
    }

    override suspend fun updateWordListWithWords(listId: Long, name: String, words: List<String>) {
        error("Not implemented in fake")
    }

    override suspend fun updateWord(word: Word) {
        lastUpdatedWord = word
        val flow = wordsByListId[word.listId] ?: return
        flow.value = flow.value.map { if (it.id == word.id) word else it }
    }

    override suspend fun resetProgress(listId: Long) {
        resetProgressCalls.add(listId)
    }

    override suspend fun resetWordProgress(wordId: Long) {
        error("Not implemented in fake")
    }

    override suspend fun resetAllProgress() {
        resetAllProgressCalls.add(Unit)
    }

    override suspend fun reconcileMastery(requiredCorrectAnswers: Int) {
        reconcileMasteryCalls.add(requiredCorrectAnswers)
    }

    override suspend fun deleteWordList(listId: Long) {
        deleteListCalls.add(listId)
        lists.value = lists.value.filterNot { it.id == listId }
    }

    override suspend fun deleteWord(wordId: Long) {
        deleteWordCalls.add(wordId)
        wordsByListId.values.forEach { flow ->
            flow.value = flow.value.filterNot { it.id == wordId }
        }
    }

    override suspend fun getWordListName(listId: Long): String? =
        lists.value.firstOrNull { it.id == listId }?.name

    override suspend fun getWordsForList(listId: Long): List<Word> =
        wordsByListId[listId]?.value.orEmpty()

    override suspend fun getWordById(wordId: Long): Word? =
        wordsByListId.values.flatMap { it.value }.firstOrNull { it.id == wordId }
}
