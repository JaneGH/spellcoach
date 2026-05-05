package com.example.spellcoach.domain.usecase

import com.example.spellcoach.domain.model.Word
import com.example.spellcoach.domain.repository.WordRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveWordsForListUseCase @Inject constructor(
    private val wordRepository: WordRepository
) {
    operator fun invoke(listId: Long): Flow<List<Word>> =
        wordRepository.observeWordsForList(listId)
}
