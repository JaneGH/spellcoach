package com.itclimb.spellcoach.domain.word

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class WordTextNormalizerTest {

    @Test
    fun normalize_lowercasesAndStripsPunctuation() {
        assertThat(WordTextNormalizer.normalize("Cat!")).isEqualTo("cat")
        assertThat(WordTextNormalizer.normalize("  HELLO  ")).isEqualTo("hello")
    }

    @Test
    fun normalize_stripsDigitsFromToken() {
        assertThat(WordTextNormalizer.normalize("hello123")).isEqualTo("hello")
    }

    @Test
    fun normalize_rejectsEmptyOrUnsupportedTokens() {
        assertThat(WordTextNormalizer.normalize("123")).isNull()
        assertThat(WordTextNormalizer.normalize("---")).isNull()
        assertThat(WordTextNormalizer.normalize("🙂")).isNull()
    }

    @Test
    fun normalize_acceptsUnicodeLettersFromAnyLanguage() {
        assertThat(WordTextNormalizer.normalize("Café")).isEqualTo("café")
        assertThat(WordTextNormalizer.normalize("Straße")).isEqualTo("straße")
        assertThat(WordTextNormalizer.normalize("привіт")).isEqualTo("привіт")
        assertThat(WordTextNormalizer.normalize("ΣΩΜΑ")).isEqualTo("σωμα")
        assertThat(WordTextNormalizer.normalize("مرحبا")).isEqualTo("مرحبا")
    }

    @Test
    fun canonicalizeForMigration_normalizesLegacyPunctuation() {
        assertThat(WordTextNormalizer.canonicalizeForMigration("  Cat!  ")).isEqualTo("cat")
    }

    @Test
    fun canonicalizeForMigration_returnsNullForNonLetterTokens() {
        assertThat(WordTextNormalizer.canonicalizeForMigration("123")).isNull()
    }

    @Test
    fun normalizeWords_deduplicatesCaseInsensitive() {
        assertThat(WordTextNormalizer.normalizeWords(listOf("Cat", "cat", "DOG")))
            .containsExactly("cat", "dog")
            .inOrder()
    }
}
