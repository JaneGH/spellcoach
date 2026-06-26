package com.itclimb.spellcoach.domain.word

import java.util.Locale

object WordTextNormalizer {

    fun normalize(token: String): String? {
        val lettersOnly = token
            .trim()
            .lowercase(Locale.ROOT)
            .filter { it.isLetter() }

        if (lettersOnly.isEmpty()) return null
        return lettersOnly
    }

    fun normalizeWords(words: List<String>): List<String> {
        val out = LinkedHashSet<String>(words.size)
        for (word in words) {
            normalize(word)?.let { out.add(it) }
        }
        return out.toList()
    }

    /**
     * Normalizes legacy DB values during migration when stored text contains stray punctuation.
     */
    fun canonicalizeForMigration(text: String): String? =
        normalize(text) ?: text
            .trim()
            .lowercase(Locale.ROOT)
            .filter { it.isLetter() }
            .takeIf { it.isNotEmpty() }
}
