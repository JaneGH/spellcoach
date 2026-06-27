package com.itclimb.spellcoach.domain.word

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.Locale

class WordScriptDetectorTest {

    @Test
    fun detectDominantScript_cyrillic() {
        val script = WordScriptDetector.detectDominantScript(listOf("привіт", "слово"))
        assertThat(script).isEqualTo(WordScript.CYRILLIC)
    }

    @Test
    fun detectDominantScript_latin() {
        val script = WordScriptDetector.detectDominantScript(listOf("hello", "world"))
        assertThat(script).isEqualTo(WordScript.LATIN)
    }

    @Test
    fun detectDominantScript_japaneseWithKana() {
        val script = WordScriptDetector.detectDominantScript(listOf("こんにちは"))
        assertThat(script).isEqualTo(WordScript.JAPANESE)
    }

    @Test
    fun detectDominantScript_korean() {
        val script = WordScriptDetector.detectDominantScript(listOf("안녕하세요"))
        assertThat(script).isEqualTo(WordScript.KOREAN)
    }

    @Test
    fun detectDominantScript_devanagari() {
        val script = WordScriptDetector.detectDominantScript(listOf("नमस्ते"))
        assertThat(script).isEqualTo(WordScript.DEVANAGARI)
    }

    @Test
    fun detectDominantScript_mixedWhenScriptsAreClose() {
        val script = WordScriptDetector.detectDominantScript(listOf("hello", "привіт"))
        assertThat(script).isEqualTo(WordScript.MIXED)
    }

    @Test
    fun resolveHandwritingScript_picksPrimaryScriptWhenMixed() {
        val script = WordScriptDetector.resolveHandwritingScript(listOf("hello", "привіт"))
        assertThat(script).isEqualTo(WordScript.CYRILLIC)
    }

    @Test
    fun resolveScript_usesDeviceLocaleWhenNoLetters() {
        val script = WordScriptDetector.resolveScript(
            texts = emptyList(),
            locale = Locale.forLanguageTag("uk-UA"),
        )
        assertThat(script).isEqualTo(WordScript.CYRILLIC)
    }

    @Test
    fun resolveHandwritingScript_usesDeviceLocaleWhenNoLetters() {
        val script = WordScriptDetector.resolveHandwritingScript(
            texts = emptyList(),
            locale = Locale.forLanguageTag("ru-RU"),
        )
        assertThat(script).isEqualTo(WordScript.CYRILLIC)
    }

    @Test
    fun scriptFromDeviceLocale_mapsCommonLanguages() {
        assertThat(WordScriptDetector.scriptFromDeviceLocale(Locale.forLanguageTag("ja-JP")))
            .isEqualTo(WordScript.JAPANESE)
        assertThat(WordScriptDetector.scriptFromDeviceLocale(Locale.forLanguageTag("zh-CN")))
            .isEqualTo(WordScript.HAN)
    }
}
