package com.itclimb.spellcoach.domain.word

import java.util.Locale

object WordScriptDetector {

    fun detectCharScript(char: Char): WordScript? {
        if (!char.isLetter()) return null
        return when (Character.UnicodeBlock.of(char)) {
            Character.UnicodeBlock.BASIC_LATIN,
            Character.UnicodeBlock.LATIN_1_SUPPLEMENT,
            Character.UnicodeBlock.LATIN_EXTENDED_A,
            Character.UnicodeBlock.LATIN_EXTENDED_B,
            Character.UnicodeBlock.LATIN_EXTENDED_ADDITIONAL -> WordScript.LATIN

            Character.UnicodeBlock.CYRILLIC,
            Character.UnicodeBlock.CYRILLIC_SUPPLEMENTARY,
            Character.UnicodeBlock.CYRILLIC_EXTENDED_A,
            Character.UnicodeBlock.CYRILLIC_EXTENDED_B -> WordScript.CYRILLIC

            Character.UnicodeBlock.GREEK,
            Character.UnicodeBlock.GREEK_EXTENDED -> WordScript.GREEK

            Character.UnicodeBlock.ARABIC,
            Character.UnicodeBlock.ARABIC_SUPPLEMENT,
            Character.UnicodeBlock.ARABIC_EXTENDED_A -> WordScript.ARABIC

            Character.UnicodeBlock.HEBREW -> WordScript.HEBREW

            Character.UnicodeBlock.HIRAGANA,
            Character.UnicodeBlock.KATAKANA -> WordScript.JAPANESE

            Character.UnicodeBlock.HANGUL_SYLLABLES,
            Character.UnicodeBlock.HANGUL_JAMO,
            Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO,
            Character.UnicodeBlock.HANGUL_JAMO_EXTENDED_A,
            Character.UnicodeBlock.HANGUL_JAMO_EXTENDED_B -> WordScript.KOREAN

            Character.UnicodeBlock.DEVANAGARI,
            Character.UnicodeBlock.DEVANAGARI_EXTENDED -> WordScript.DEVANAGARI

            Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS,
            Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A,
            Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B,
            Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS,
            Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT -> WordScript.HAN

            else -> null
        }
    }

    fun detectDominantScript(texts: Iterable<String>): WordScript {
        val counts = mutableMapOf<WordScript, Int>()
        for (text in texts) {
            for (char in text) {
                val script = detectCharScript(char) ?: continue
                counts[script] = counts.getOrDefault(script, 0) + 1
            }
        }
        if (counts.isEmpty()) return WordScript.LATIN

        val ranked = counts.entries.sortedByDescending { it.value }
        val top = ranked.first()
        if (ranked.size == 1) return top.key

        val runnerUp = ranked[1]
        return if (runnerUp.value.toFloat() / top.value.toFloat() >= 0.25f) {
            WordScript.MIXED
        } else {
            top.key
        }
    }

    fun scriptFromDeviceLocale(locale: Locale = Locale.getDefault()): WordScript {
        val language = locale.language.lowercase(Locale.ROOT)
        return when (language) {
            "uk", "ru", "bg", "sr", "mk" -> WordScript.CYRILLIC
            "el" -> WordScript.GREEK
            "ar" -> WordScript.ARABIC
            "he", "iw" -> WordScript.HEBREW
            "ja" -> WordScript.JAPANESE
            "ko" -> WordScript.KOREAN
            "hi", "mr", "ne" -> WordScript.DEVANAGARI
            "zh" -> WordScript.HAN
            else -> WordScript.LATIN
        }
    }

    fun resolveScript(texts: Iterable<String>, locale: Locale = Locale.getDefault()): WordScript {
        val detected = detectDominantScript(texts)
        return if (detected == WordScript.LATIN && texts.none { it.any(Char::isLetter) }) {
            scriptFromDeviceLocale(locale)
        } else {
            detected
        }
    }

    /** For handwriting: never returns [WordScript.MIXED] — picks the dominant script instead. */
    fun resolveHandwritingScript(texts: Iterable<String>, locale: Locale = Locale.getDefault()): WordScript {
        val detected = detectDominantScript(texts)
        val script = if (detected == WordScript.MIXED) {
            detectPrimaryScript(texts)
        } else {
            detected
        }
        return if (script == WordScript.LATIN && texts.none { it.any(Char::isLetter) }) {
            scriptFromDeviceLocale(locale)
        } else {
            script
        }
    }

    private fun detectPrimaryScript(texts: Iterable<String>): WordScript {
        val counts = mutableMapOf<WordScript, Int>()
        for (text in texts) {
            for (char in text) {
                val script = detectCharScript(char) ?: continue
                counts[script] = counts.getOrDefault(script, 0) + 1
            }
        }
        return counts.maxByOrNull { it.value }?.key ?: WordScript.LATIN
    }
}
