package com.itclimb.spellcoach.data.mlkit

import com.google.mlkit.common.MlKitException
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognitionModelIdentifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class InkModelDownloader(private val languageTag: String) {
    @Volatile
    private var modelReady = false
    private val mutex = Mutex()

    private val model: DigitalInkRecognitionModel? by lazy {
        val id = modelIdentifierForTag(languageTag) ?: return@lazy null
        DigitalInkRecognitionModel.builder(id).build()
    }

    private fun modelIdentifierForTag(tag: String): DigitalInkRecognitionModelIdentifier? =
        runCatching { DigitalInkRecognitionModelIdentifier.fromLanguageTag(tag) }
            .getOrElse { error ->
                if (error is MlKitException) null else throw error
            }

    suspend fun ensureInkModelDownloaded() {
        if (modelReady) return
        val inkModel = model ?: return

        mutex.withLock {
            if (modelReady) return@withLock

            val downloaded = runCatching {
                withContext(Dispatchers.Default) {
                    val conditions = DownloadConditions.Builder().build()
                    RemoteModelManager.getInstance()
                        .download(inkModel, conditions)
                        .await()
                }
            }.isSuccess

            if (downloaded) {
                modelReady = true
            }
        }
    }
}
