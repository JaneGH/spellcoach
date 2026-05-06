package com.example.spellcoach.presentation.addwords

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spellcoach.domain.usecase.CreateWordListUseCase
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.math.min
import java.util.Locale
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@HiltViewModel
class AddWordsViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val createWordList: CreateWordListUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddWordsState())
    val state: StateFlow<AddWordsState> = _state.asStateFlow()

    private val _events = Channel<AddWordsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun setListName(value: String) {
        _state.value = _state.value.copy(listName = value, errorMessage = null)
    }

    fun setRawInput(value: String) {
        _state.value = _state.value.copy(rawInput = value, errorMessage = null)
    }

    fun addParsedWordsFromInput() {
        val words = parseWords(_state.value.rawInput)
        if (words.isEmpty()) return
        val merged = mergeDistinctWords(_state.value.previewWords, words)
        _state.value = _state.value.copy(previewWords = merged, rawInput = "", errorMessage = null)
    }

    fun removeWord(word: String) {
        _state.value = _state.value.copy(
            previewWords = _state.value.previewWords.filter { it != word }
        )
    }

    fun importFromPdf(uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            if (_state.value.isImporting) return@launch
            _state.value = _state.value.copy(isImporting = true, errorMessage = null)
            val result = runCatching { extractWordsFromPdf(uri) }
            result.fold(
                onSuccess = { imported ->
                    val merged = mergeDistinctWords(_state.value.previewWords, imported)
                    _state.value = _state.value.copy(
                        previewWords = merged,
                        isImporting = false,
                        errorMessage = if (imported.isEmpty()) "No words found in PDF." else null
                    )
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isImporting = false,
                        errorMessage = e.message ?: "Could not import from PDF."
                    )
                }
            )
        }
    }

    fun importFromImage(uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            if (_state.value.isImporting) return@launch
            _state.value = _state.value.copy(isImporting = true, errorMessage = null)
            val result = runCatching { extractWordsFromImage(uri) }
            result.fold(
                onSuccess = { imported ->
                    val merged = mergeDistinctWords(_state.value.previewWords, imported)
                    _state.value = _state.value.copy(
                        previewWords = merged,
                        isImporting = false,
                        errorMessage = if (imported.isEmpty()) "No words found in image." else null
                    )
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isImporting = false,
                        errorMessage = e.message ?: "Could not scan photo."
                    )
                }
            )
        }
    }

    fun save() {
        viewModelScope.launch {
            if (_state.value.previewWords.isEmpty()) {
                _state.value = _state.value.copy(errorMessage = "Add at least one word.")
                return@launch
            }
            _state.value = _state.value.copy(saving = true, errorMessage = null)
            val result = createWordList(_state.value.listName, _state.value.previewWords)
            result.fold(
                onSuccess = {
                    _state.value = _state.value.copy(saving = false)
                    _events.send(AddWordsEvent.Saved)
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        saving = false,
                        errorMessage = when (e.message) {
                            "empty_name" -> "Please enter a list name."
                            "no_words" -> "Add at least one word."
                            else -> e.message ?: "Could not save."
                        }
                    )
                }
            )
        }
    }

    private fun parseWords(input: String): List<String> {
        val tokens = input.split(Regex("[,\\s]+"))
        return normalizeAndFilterWords(tokens)
    }

    private fun mergeDistinctWords(existing: List<String>, incoming: List<String>): List<String> {
        if (incoming.isEmpty()) return existing
        val set = LinkedHashSet<String>(existing.size + incoming.size)
        existing.forEach { set.add(it) }
        incoming.forEach { set.add(it) }
        return set.toList()
    }

    private fun normalizeAndFilterWords(tokens: List<String>): List<String> {
        val out = LinkedHashSet<String>()
        for (t in tokens) {
            val w = normalizeWord(t) ?: continue
            out.add(w)
        }
        return out.toList()
    }

    private fun normalizeWord(token: String): String? {
        // Keep only English + Ukrainian letters, strip punctuation/digits.
        val lettersOnly = token
            .trim()
            .lowercase(Locale.ROOT)
            .filter { it.isLetter() }

        if (lettersOnly.isEmpty()) return null
        if (!lettersOnly.all { isSupportedLetter(it) }) return null
        return lettersOnly
    }

    private fun isSupportedLetter(c: Char): Boolean =
        (c in 'a'..'z') ||
            (c in 'A'..'Z') ||
            (c in 'а'..'я') ||
            (c in 'А'..'Я') ||
            c == 'і' || c == 'І' ||
            c == 'ї' || c == 'Ї' ||
            c == 'є' || c == 'Є' ||
            c == 'ґ' || c == 'Ґ'

    private suspend fun extractWordsFromImage(uri: Uri): List<String> = withContext(Dispatchers.IO) {
        val image = InputImage.fromFilePath(appContext, uri)
        val text = textRecognizer.process(image).await()
        val tokens = text.text.split(Regex("\\s+"))
        normalizeAndFilterWords(tokens)
    }

    private suspend fun extractWordsFromPdf(uri: Uri): List<String> = withContext(Dispatchers.IO) {
        val pfd = appContext.contentResolver.openFileDescriptor(uri, "r")
            ?: throw IllegalStateException("Could not open PDF.")

        pfd.use {
            PdfRenderer(it).use { renderer ->
                val all = LinkedHashSet<String>()
                for (i in 0 until renderer.pageCount) {
                    renderer.openPage(i).use { page ->
                        val bitmap = renderPdfPageToBitmap(page)
                        val recognized = textRecognizer.process(InputImage.fromBitmap(bitmap, 0)).await()
                        val tokens = recognized.text.split(Regex("\\s+"))
                        normalizeAndFilterWords(tokens).forEach { all.add(it) }
                        bitmap.recycle()
                    }
                }
                all.toList()
            }
        }
    }

    private fun renderPdfPageToBitmap(page: PdfRenderer.Page): Bitmap {
        // Render at ~2x but keep bitmap bounded to avoid OOM.
        val baseW = page.width.coerceAtLeast(1)
        val baseH = page.height.coerceAtLeast(1)
        val maxDim = 2048f
        val desiredScale = 2f
        val scale = min(desiredScale, min(maxDim / baseW.toFloat(), maxDim / baseH.toFloat()))

        val w = (baseW * scale).toInt().coerceAtLeast(1)
        val h = (baseH * scale).toInt().coerceAtLeast(1)

        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(android.graphics.Color.WHITE)

        val matrix = Matrix().apply { postScale(scale, scale) }
        page.render(bitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        return bitmap
    }
}
