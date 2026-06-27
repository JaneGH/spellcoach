package com.itclimb.spellcoach.data.mlkit

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.itclimb.spellcoach.domain.word.WordScript
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed interface OcrRecognitionResult {
    data class Success(val text: String) : OcrRecognitionResult
    data class Unsupported(val script: WordScript) : OcrRecognitionResult
}

class MultilingualOcrClient @Inject constructor() {

    private val latinRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private var chineseRecognizer: TextRecognizer? = null
    private var japaneseRecognizer: TextRecognizer? = null
    private var koreanRecognizer: TextRecognizer? = null
    private var devanagariRecognizer: TextRecognizer? = null

    suspend fun recognize(image: InputImage, wordScript: WordScript): OcrRecognitionResult =
        withContext(Dispatchers.IO) {
            val ocrScript = MlKitRecognitionMapping.ocrScriptFor(wordScript)
            if (ocrScript == OcrScript.UNSUPPORTED) {
                return@withContext OcrRecognitionResult.Unsupported(wordScript)
            }
            val recognizer = recognizerFor(ocrScript)
            val text = recognizer.process(image).await().text
            OcrRecognitionResult.Success(text)
        }

    fun close() {
        latinRecognizer.close()
        chineseRecognizer?.close()
        japaneseRecognizer?.close()
        koreanRecognizer?.close()
        devanagariRecognizer?.close()
        chineseRecognizer = null
        japaneseRecognizer = null
        koreanRecognizer = null
        devanagariRecognizer = null
    }

    private fun recognizerFor(script: OcrScript): TextRecognizer = when (script) {
        OcrScript.LATIN -> latinRecognizer
        OcrScript.CHINESE -> chineseRecognizer ?: TextRecognition.getClient(
            ChineseTextRecognizerOptions.Builder().build()
        ).also { chineseRecognizer = it }

        OcrScript.JAPANESE -> japaneseRecognizer ?: TextRecognition.getClient(
            JapaneseTextRecognizerOptions.Builder().build()
        ).also { japaneseRecognizer = it }

        OcrScript.KOREAN -> koreanRecognizer ?: TextRecognition.getClient(
            KoreanTextRecognizerOptions.Builder().build()
        ).also { koreanRecognizer = it }

        OcrScript.DEVANAGARI -> devanagariRecognizer ?: TextRecognition.getClient(
            DevanagariTextRecognizerOptions.Builder().build()
        ).also { devanagariRecognizer = it }

        OcrScript.UNSUPPORTED -> error("Unsupported OCR script")
    }
}
