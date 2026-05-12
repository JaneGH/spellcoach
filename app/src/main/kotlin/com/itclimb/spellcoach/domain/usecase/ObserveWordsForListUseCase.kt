package com.itclimb.spellcoach.domain.usecase

import com.itclimb.spellcoach.domain.model.Word
import com.itclimb.spellcoach.domain.repository.WordRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveWordsForListUseCase @Inject constructor(
    private val wordRepository: WordRepository
) {
    operator fun invoke(listId: Long): Flow<List<Word>> =
        wordRepository.observeWordsForList(listId)
}
