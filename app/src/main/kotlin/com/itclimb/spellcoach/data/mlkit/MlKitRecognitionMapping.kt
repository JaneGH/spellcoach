package com.itclimb.spellcoach.data.mlkit

import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognitionModelIdentifier
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognizer
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognizerOptions
import com.itclimb.spellcoach.domain.word.WordScript
import java.util.Locale

enum class OcrScript {
    LATIN,
    CHINESE,
    JAPANESE,
    KOREAN,
    DEVANAGARI,
    UNSUPPORTED,
}

object MlKitRecognitionMapping {

    fun ocrScriptFor(wordScript: WordScript): OcrScript = when (wordScript) {
        WordScript.LATIN,
        WordScript.GREEK,
        WordScript.MIXED -> OcrScript.LATIN

        WordScript.HAN -> OcrScript.CHINESE
        WordScript.JAPANESE -> OcrScript.JAPANESE
        WordScript.KOREAN -> OcrScript.KOREAN
        WordScript.DEVANAGARI -> OcrScript.DEVANAGARI

        WordScript.CYRILLIC,
        WordScript.ARABIC,
        WordScript.HEBREW -> OcrScript.UNSUPPORTED
    }

    fun handwritingLanguageTags(wordScript: WordScript, locale: Locale = Locale.getDefault()): List<String> {
        val deviceTag = locale.toLanguageTag()
        val deviceLang = locale.language.lowercase(Locale.ROOT)
        return when (wordScript) {
            WordScript.CYRILLIC -> cyrillicHandwritingTags(deviceLang, deviceTag)
            WordScript.LATIN -> listOf(deviceTag, "en", "en-US", "en-GB", "es", "fr", "de")
            WordScript.GREEK -> listOf("el", "el-GR", deviceTag, "en-US")
            WordScript.ARABIC -> listOf("ar", deviceTag, "en-US")
            WordScript.HEBREW -> listOf("he", "he-IL", deviceTag, "en-US")
            WordScript.HAN -> listOf("zh-Hani", "zh", "zh-Hans", deviceTag, "en-US")
            WordScript.JAPANESE -> listOf("ja", "ja-JP", deviceTag, "en-US")
            WordScript.KOREAN -> listOf("ko", "ko-KR", deviceTag, "en-US")
            WordScript.DEVANAGARI -> listOf("hi", "hi-IN", deviceTag, "en-US")
            WordScript.MIXED -> listOf(deviceTag, "en", "en-US")
        }.distinct()
    }

    private fun cyrillicHandwritingTags(deviceLang: String, deviceTag: String): List<String> = when (deviceLang) {
        "ru" -> listOf("ru", "ru-RU", "uk", "uk-UA", deviceTag, "en-US")
        "uk" -> listOf("uk", "uk-UA", "ru", "ru-RU", deviceTag, "en-US")
        else -> listOf("ru", "ru-RU", "uk", "uk-UA", deviceTag, "en-US")
    }

    fun resolveHandwritingLanguageTag(
        wordScript: WordScript,
        locale: Locale = Locale.getDefault(),
    ): String? = handwritingLanguageTags(wordScript, locale).firstNotNullOfOrNull(::isSupportedHandwritingTag)

    fun createHandwritingRecognizer(languageTag: String): DigitalInkRecognizer? {
        val modelId = modelIdentifierForTag(languageTag) ?: return null
        val model = DigitalInkRecognitionModel.builder(modelId).build()
        return DigitalInkRecognition.getClient(
            DigitalInkRecognizerOptions.builder(model).build()
        )
    }

    private fun isSupportedHandwritingTag(tag: String): String? =
        modelIdentifierForTag(tag)?.let { tag }

    private fun modelIdentifierForTag(tag: String): DigitalInkRecognitionModelIdentifier? =
        runCatching { DigitalInkRecognitionModelIdentifier.fromLanguageTag(tag) }
            .getOrElse { error ->
                if (error is MlKitException) null else throw error
            }
}
