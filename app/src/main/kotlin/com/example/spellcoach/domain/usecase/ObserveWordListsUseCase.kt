package com.example.spellcoach.domain.usecase

import com.example.spellcoach.domain.model.WordList
import com.example.spellcoach.domain.repository.WordRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveWordListsUseCase @Inject constructor(
    private val wordRepository: WordRepository
) {
    operator fun invoke(): Flow<List<WordList>> = wordRepository.observeWordLists()
}
