package com.itclimb.spellcoach.domain.usecase

import com.itclimb.spellcoach.domain.model.WordList
import com.itclimb.spellcoach.domain.repository.WordRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveWordListsUseCase @Inject constructor(
    private val wordRepository: WordRepository
) {
    operator fun invoke(): Flow<List<WordList>> = wordRepository.observeWordLists()
}
