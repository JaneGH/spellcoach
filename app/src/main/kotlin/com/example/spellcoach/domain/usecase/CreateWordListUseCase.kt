package com.example.spellcoach.domain.usecase

import com.example.spellcoach.domain.repository.WordRepository
import javax.inject.Inject

class CreateWordListUseCase @Inject constructor(
    private val wordRepository: WordRepository
) {
    suspend operator fun invoke(name: String, words: List<String>): Result<Long> {
        val n = name.trim()
        if (n.isEmpty()) return Result.failure(IllegalArgumentException("empty_name"))
        val parsed = words.map { it.trim() }.filter { it.isNotEmpty() }.distinct()
        if (parsed.isEmpty()) return Result.failure(IllegalArgumentException("no_words"))
        return runCatching { wordRepository.createWordListWithWords(n, parsed) }
    }
}
