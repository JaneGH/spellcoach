package com.itclimb.spellcoach.data.mlkit

import com.google.common.truth.Truth.assertThat
import com.itclimb.spellcoach.domain.word.WordScript
import org.junit.Test
import java.util.Locale

class MlKitRecognitionMappingTest {

    @Test
    fun ocrScriptFor_mapsScriptsToMlKitModules() {
        assertThat(MlKitRecognitionMapping.ocrScriptFor(WordScript.LATIN)).isEqualTo(OcrScript.LATIN)
        assertThat(MlKitRecognitionMapping.ocrScriptFor(WordScript.GREEK)).isEqualTo(OcrScript.LATIN)
        assertThat(MlKitRecognitionMapping.ocrScriptFor(WordScript.HAN)).isEqualTo(OcrScript.CHINESE)
        assertThat(MlKitRecognitionMapping.ocrScriptFor(WordScript.JAPANESE)).isEqualTo(OcrScript.JAPANESE)
        assertThat(MlKitRecognitionMapping.ocrScriptFor(WordScript.KOREAN)).isEqualTo(OcrScript.KOREAN)
        assertThat(MlKitRecognitionMapping.ocrScriptFor(WordScript.DEVANAGARI)).isEqualTo(OcrScript.DEVANAGARI)
        assertThat(MlKitRecognitionMapping.ocrScriptFor(WordScript.CYRILLIC)).isEqualTo(OcrScript.UNSUPPORTED)
        assertThat(MlKitRecognitionMapping.ocrScriptFor(WordScript.ARABIC)).isEqualTo(OcrScript.UNSUPPORTED)
        assertThat(MlKitRecognitionMapping.ocrScriptFor(WordScript.HEBREW)).isEqualTo(OcrScript.UNSUPPORTED)
        assertThat(MlKitRecognitionMapping.ocrScriptFor(WordScript.MIXED)).isEqualTo(OcrScript.LATIN)
    }

    @Test
    fun handwritingLanguageTags_prefersRussianForRuLocale() {
        val tags = MlKitRecognitionMapping.handwritingLanguageTags(
            WordScript.CYRILLIC,
            Locale.forLanguageTag("ru-RU"),
        )
        assertThat(tags.first()).isEqualTo("ru")
        assertThat(tags).contains("uk")
    }

    @Test
    fun handwritingLanguageTags_prefersUkrainianForUkLocale() {
        val tags = MlKitRecognitionMapping.handwritingLanguageTags(
            WordScript.CYRILLIC,
            Locale.forLanguageTag("uk-UA"),
        )
        assertThat(tags.first()).isEqualTo("uk")
        assertThat(tags).contains("ru")
    }
}
