package com.itclimb.spellcoach.domain.usecase

import com.itclimb.spellcoach.domain.repository.WordRepository
import com.itclimb.spellcoach.domain.word.WordTextNormalizer
import javax.inject.Inject

class CreateWordListUseCase @Inject constructor(
    private val wordRepository: WordRepository
) {
    suspend operator fun invoke(name: String, words: List<String>): Result<Long> {
        val n = name.trim()
        if (n.isEmpty()) return Result.failure(IllegalArgumentException("empty_name"))
        val parsed = WordTextNormalizer.normalizeWords(words)
        if (parsed.isEmpty()) return Result.failure(IllegalArgumentException("no_words"))
        return runCatching { wordRepository.createWordListWithWords(n, parsed) }
    }
}
