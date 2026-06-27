package com.itclimb.spellcoach.feature.addwords.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itclimb.spellcoach.R
import com.itclimb.spellcoach.data.mlkit.MultilingualOcrClient
import com.itclimb.spellcoach.data.mlkit.OcrRecognitionResult
import com.itclimb.spellcoach.data.mlkit.MlKitRecognitionMapping
import com.itclimb.spellcoach.data.mlkit.OcrScript
import com.itclimb.spellcoach.domain.repository.DuplicateWordInListException
import com.itclimb.spellcoach.domain.repository.WordRepository
import com.itclimb.spellcoach.domain.usecase.CreateWordListUseCase
import com.itclimb.spellcoach.domain.word.WordScriptDetector
import com.itclimb.spellcoach.domain.word.WordTextNormalizer
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.min

@HiltViewModel
class AddWordsViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    savedStateHandle: SavedStateHandle,
    private val wordRepository: WordRepository,
    private val createWordList: CreateWordListUseCase,
    private val ocrClient: MultilingualOcrClient,
) : ViewModel() {

    private val _state = MutableStateFlow(AddWordsState())
    val state: StateFlow<AddWordsState> = _state.asStateFlow()

    private val _events = Channel<AddWordsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    override fun onCleared() {
        ocrClient.close()
        super.onCleared()
    }

    private val listIdArg: Long? = savedStateHandle.get<Long>("listId")?.takeIf { it > 0 }

    init {
        if (listIdArg != null) {
            viewModelScope.launch {
                val name = wordRepository.getWordListName(listIdArg).orEmpty()
                val words = wordRepository.getWordsForList(listIdArg).map { it.text }
                _state.value = _state.value.copy(
                    listId = listIdArg,
                    isEditMode = true,
                    listName = name,
                    previewWords = words
                )
            }
        }
    }

    fun setListName(value: String) {
        _state.value = _state.value.copy(listName = value, errorMessage = null, importNotice = null)
    }

    fun setRawInput(value: String) {
        _state.value = _state.value.copy(rawInput = value, errorMessage = null, importNotice = null)
    }

    fun addParsedWordsFromInput() {
        val words = parseWords(_state.value.rawInput)
        if (words.isEmpty()) return
        val merged = mergeDistinctWords(_state.value.previewWords, words)
        _state.value = _state.value.copy(
            previewWords = merged,
            rawInput = "",
            errorMessage = null,
            importNotice = null,
        )
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
            _state.value = _state.value.copy(isImporting = true, errorMessage = null, importNotice = null)
            val previewWords = _state.value.previewWords
            val result = runCatching { extractWordsFromPdf(uri, previewWords) }
            result.fold(
                onSuccess = { extraction ->
                    val merged = mergeDistinctWords(previewWords, extraction.words)
                    _state.value = _state.value.copy(
                        previewWords = merged,
                        isImporting = false,
                        errorMessage = if (extraction.words.isEmpty() && extraction.notice == null) {
                            appContext.getString(R.string.error_no_words_pdf)
                        } else {
                            null
                        },
                        importNotice = extraction.notice,
                    )
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isImporting = false,
                        errorMessage = e.message ?: appContext.getString(R.string.error_import_pdf_generic)
                    )
                }
            )
        }
    }

    fun importFromImage(uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            if (_state.value.isImporting) return@launch
            _state.value = _state.value.copy(isImporting = true, errorMessage = null, importNotice = null)
            val previewWords = _state.value.previewWords
            val result = runCatching { extractWordsFromImage(uri, previewWords) }
            result.fold(
                onSuccess = { extraction ->
                    val merged = mergeDistinctWords(previewWords, extraction.words)
                    _state.value = _state.value.copy(
                        previewWords = merged,
                        isImporting = false,
                        errorMessage = if (extraction.words.isEmpty() && extraction.notice == null) {
                            appContext.getString(R.string.error_no_words_image)
                        } else {
                            null
                        },
                        importNotice = extraction.notice,
                    )
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isImporting = false,
                        errorMessage = e.message ?: appContext.getString(R.string.error_scan_photo_generic)
                    )
                }
            )
        }
    }

    fun save() {
        viewModelScope.launch {
            if (_state.value.saving) return@launch
            if (_state.value.previewWords.isEmpty()) {
                _state.value = _state.value.copy(
                    errorMessage = appContext.getString(R.string.error_add_at_least_one_word)
                )
                return@launch
            }
            _state.value = _state.value.copy(saving = true, errorMessage = null)
            val editId = _state.value.listId
            if (editId != null) {
                val n = _state.value.listName.trim()
                if (n.isEmpty()) {
                    _state.value = _state.value.copy(
                        saving = false,
                        errorMessage = appContext.getString(R.string.error_list_name_required)
                    )
                    return@launch
                }
                val parsed = WordTextNormalizer.normalizeWords(_state.value.previewWords)
                if (parsed.isEmpty()) {
                    _state.value = _state.value.copy(
                        saving = false,
                        errorMessage = appContext.getString(R.string.error_add_at_least_one_word)
                    )
                    return@launch
                }
                runCatching {
                    wordRepository.updateWordListWithWords(editId, n, parsed)
                }.fold(
                    onSuccess = {
                        _state.value = _state.value.copy(saving = false)
                        _events.send(AddWordsEvent.Saved)
                    },
                    onFailure = { e ->
                        _state.value = _state.value.copy(
                            saving = false,
                            errorMessage = wordSaveErrorMessage(e),
                        )
                    }
                )
            } else {
                val result = createWordList(_state.value.listName, _state.value.previewWords)
                result.fold(
                    onSuccess = {
                        _state.value = _state.value.copy(saving = false)
                        _events.send(AddWordsEvent.Saved)
                    },
                    onFailure = { e ->
                        _state.value = _state.value.copy(
                            saving = false,
                            errorMessage = wordSaveErrorMessage(e),
                        )
                    }
                )
            }
        }
    }

    private fun wordSaveErrorMessage(error: Throwable): String = when {
        error is DuplicateWordInListException -> "This list contains duplicate words."
        error.message == "duplicate_word_in_list" -> "This list contains duplicate words."
        error.message == "empty_name" -> appContext.getString(R.string.error_list_name_required)
        error.message == "no_words" -> appContext.getString(R.string.error_add_at_least_one_word)
        else -> error.message ?: appContext.getString(R.string.error_save_generic)
    }

    private fun parseWords(input: String): List<String> {
        val tokens = input.split(Regex("[,\\s]+"))
        return WordTextNormalizer.normalizeWords(tokens)
    }

    private fun mergeDistinctWords(existing: List<String>, incoming: List<String>): List<String> {
        if (incoming.isEmpty()) return existing
        val set = LinkedHashSet<String>(existing.size + incoming.size)
        existing.forEach { set.add(it) }
        incoming.forEach { word -> WordTextNormalizer.normalize(word)?.let { set.add(it) } }
        return set.toList()
    }

    private fun ocrScriptFor(previewWords: List<String>) =
        MlKitRecognitionMapping.ocrScriptFor(WordScriptDetector.resolveScript(previewWords))

    private fun unsupportedOcrNotice(): String =
        appContext.getString(R.string.add_words_ocr_unsupported_script)

    private suspend fun extractWordsFromImage(uri: Uri, previewWords: List<String>): ImportExtractionResult =
        withContext(Dispatchers.IO) {
            if (ocrScriptFor(previewWords) == OcrScript.UNSUPPORTED) {
                return@withContext ImportExtractionResult(
                    words = emptyList(),
                    notice = unsupportedOcrNotice(),
                )
            }
            val image = InputImage.fromFilePath(appContext, uri)
            when (val result = ocrClient.recognize(image, WordScriptDetector.resolveScript(previewWords))) {
                is OcrRecognitionResult.Unsupported -> ImportExtractionResult(
                    words = emptyList(),
                    notice = unsupportedOcrNotice(),
                )

                is OcrRecognitionResult.Success -> ImportExtractionResult(
                    words = wordsFromOcrText(result.text),
                    notice = null,
                )
            }
        }

    private suspend fun extractWordsFromPdf(uri: Uri, previewWords: List<String>): ImportExtractionResult =
        withContext(Dispatchers.IO) {
            if (ocrScriptFor(previewWords) == OcrScript.UNSUPPORTED) {
                return@withContext ImportExtractionResult(
                    words = emptyList(),
                    notice = unsupportedOcrNotice(),
                )
            }
            val wordScript = WordScriptDetector.resolveScript(previewWords)
            val pfd = appContext.contentResolver.openFileDescriptor(uri, "r")
                ?: throw IllegalStateException("Could not open PDF.")

            pfd.use {
                PdfRenderer(it).use { renderer ->
                    val all = LinkedHashSet<String>()
                    val pagesToScan = min(renderer.pageCount, MAX_PDF_PAGES)
                    for (i in 0 until pagesToScan) {
                        renderer.openPage(i).use { page ->
                            val bitmap = renderPdfPageToBitmap(page)
                            when (val result = ocrClient.recognize(InputImage.fromBitmap(bitmap, 0), wordScript)) {
                                is OcrRecognitionResult.Unsupported -> {
                                    bitmap.recycle()
                                    return@withContext ImportExtractionResult(
                                        words = emptyList(),
                                        notice = unsupportedOcrNotice(),
                                    )
                                }

                                is OcrRecognitionResult.Success -> {
                                    wordsFromOcrText(result.text).forEach { word -> all.add(word) }
                                }
                            }
                            bitmap.recycle()
                        }
                    }
                    val truncationNotice = if (renderer.pageCount > MAX_PDF_PAGES) {
                        appContext.getString(R.string.add_words_pdf_pages_truncated, MAX_PDF_PAGES)
                    } else {
                        null
                    }
                    ImportExtractionResult(
                        words = all.toList(),
                        notice = truncationNotice,
                    )
                }
            }
        }

    private fun wordsFromOcrText(text: String): List<String> {
        val tokens = text.split(Regex("\\s+"))
        return WordTextNormalizer.normalizeWords(tokens)
    }

    private data class ImportExtractionResult(
        val words: List<String>,
        val notice: String?,
    )

    private companion object {
        const val MAX_PDF_PAGES = 20
    }

    private fun renderPdfPageToBitmap(page: PdfRenderer.Page): Bitmap {
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
