package com.itclimb.spellcoach.feature.practice.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itclimb.spellcoach.domain.practice.PracticeResultBuffer
import com.itclimb.spellcoach.domain.practice.SpellingComparer
import com.itclimb.spellcoach.domain.practice.SpellingFeedback
import com.itclimb.spellcoach.domain.speech.RewardSoundPlayer
import com.itclimb.spellcoach.domain.speech.SpellCoachTextToSpeech
import com.itclimb.spellcoach.domain.model.Badge
import com.itclimb.spellcoach.domain.model.PracticeResult
import com.itclimb.spellcoach.domain.model.Word
import com.itclimb.spellcoach.domain.model.isLearnedAtThreshold
import com.itclimb.spellcoach.domain.repository.RewardRepository
import com.itclimb.spellcoach.domain.repository.WordRepository
import com.itclimb.spellcoach.domain.usecase.ObserveSettingsUseCase
import com.itclimb.spellcoach.domain.usecase.ProcessSpellingResultUseCase
import com.itclimb.spellcoach.feature.practice.PracticeListHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlin.random.Random

data class PracticeWordReviewMeta(
    val wordId: Long,
    val incorrectAttempts: Int = 0,
    val lastSeenTimestamp: Long = 0L,
    val needsReview: Boolean = false
)

enum class WordMasteryLevel {
    NEW,
    LEARNING,
    FAMILIAR,
    MASTERED
}

data class PracticeUiState(
    val listId: Long = 0L,
    val listIdValid: Boolean = false,
    /** True when an explicit pending session targets a different list than this route. */
    val sessionWriteBlocked: Boolean = false,
    val listName: String = "",
    val allWords: List<Word> = emptyList(),
    val words: List<Word> = emptyList(),
    val currentIndex: Int = 0,
    val input: String = "",
    val letters: List<String> = emptyList(),
    val hintsEnabled: Boolean = true,
    val showHints: Boolean = false,
    val sessionCorrect: Int = 0,
    val incorrectSubmissions: Int = 0,
    val loading: Boolean = true,
    val feedbackCorrect: Boolean? = null,
    val spellingFeedback: SpellingFeedback? = null,
    val animationHint: PracticeAnimHint = PracticeAnimHint.None,
    val answerSoundsEnabled: Boolean = true,
    val requiredCorrectAnswers: Int = 3,
    val excludeMasteredWords: Boolean = false,
    /** When true, mastered words are included for this session (overrides [excludeMasteredWords]). */
    val includeMasteredInSession: Boolean = false,
    val wordJustMastered: Boolean = false,
    val lastWordId: Long? = null,
    val selectionStep: Long = 0L, // increments when user taps "Next word"

    // Short session loop metadata
    val sessionTargetSelections: Int = 0,
    val sessionComplete: Boolean = false,

    // Lightweight in-memory review metadata (not persisted)
    val reviewMetaByWordId: Map<Long, PracticeWordReviewMeta> = emptyMap(),

    // Derived session stats for completion card copy
    val masteredWordsCount: Int = 0,
    val wordsNeedingReviewCount: Int = 0
)

enum class PracticeAnimHint { None, BounceOk, ShakeWrong }

sealed interface PracticeEvent {
    data object Finished : PracticeEvent
    /** Emitted when a write was blocked because this route is stale vs a pending session. */
    data object StaleSessionBlocked : PracticeEvent
}

@HiltViewModel
class PracticeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val wordRepository: WordRepository,
    private val processSpelling: ProcessSpellingResultUseCase,
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val rewardRepository: RewardRepository,
    private val practiceResultBuffer: PracticeResultBuffer,
    private val sound: RewardSoundPlayer,
    private val tts: SpellCoachTextToSpeech,
    private val practiceListHolder: PracticeListHolder,
) : ViewModel() {

    private val listId: Long = savedStateHandle.get<Long>("listId") ?: 0L
    private val listIdValid: Boolean = listId > 0L

    private val sessionBadges = mutableListOf<Badge>()
    private var isSessionFinalized: Boolean = false
    private val checkWordMutex = Mutex()

    private val _state = MutableStateFlow(
        PracticeUiState(
            listId = listId,
            listIdValid = listIdValid,
            loading = listIdValid,
        )
    )
    val state: StateFlow<PracticeUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<PracticeEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<PracticeEvent> = _events.asSharedFlow()

    init {
        when {
            !listIdValid -> _state.update { it.copy(loading = false) }
            !isSessionAuthorizedForWrites() -> {
                _state.update {
                    it.copy(loading = false, sessionWriteBlocked = true)
                }
            }
            else -> loadPracticeSession()
        }
    }

    /**
     * Route [listId] is authoritative. When the user explicitly requested a different pending
     * session, block all progress writes for this stale entry.
     */
    private fun isSessionAuthorizedForWrites(): Boolean {
        if (!listIdValid) return false
        return practiceListHolder.isExplicitSessionReady(listId)
    }

    private fun blockWriteOperation(): Boolean {
        if (isSessionAuthorizedForWrites()) return false
        _state.update { it.copy(sessionWriteBlocked = true) }
        _events.tryEmit(PracticeEvent.StaleSessionBlocked)
        return true
    }

    private fun loadPracticeSession() {
        viewModelScope.launch {
            val name = wordRepository.getWordListName(listId).orEmpty()
            _state.update {
                it.copy(
                    listName = name,
                    loading = false,
                    showHints = false
                )
            }
        }
        viewModelScope.launch {
            val settingsFlow = observeSettingsUseCase()
            settingsFlow.collect { s ->
                _state.update { cur ->
                    val excludeChanged =
                        cur.excludeMasteredWords != s.excludeMasteredWordsFromPractice
                    cur.copy(
                        answerSoundsEnabled = s.answerSoundsEnabled,
                        hintsEnabled = s.letterHintsEnabled,
                        showHints = if (s.letterHintsEnabled) cur.showHints else false,
                        requiredCorrectAnswers = s.requiredCorrectAnswers,
                        excludeMasteredWords = s.excludeMasteredWordsFromPractice,
                        includeMasteredInSession = if (excludeChanged) {
                            false
                        } else {
                            cur.includeMasteredInSession
                        }
                    )
                }
                tts.setSpeechRate(s.speechRate)
            }
        }

        viewModelScope.launch {
            observeSettingsUseCase()
                .map { it.requiredCorrectAnswers }
                .distinctUntilChanged()
                .collect { required ->
                    wordRepository.reconcileMastery(required)
                }
        }

        viewModelScope.launch {
            val settingsFlow = observeSettingsUseCase()
            val wordsFlow = wordRepository.observeWordsForList(listId)

            combine(
                wordsFlow,
                settingsFlow
                    .map {
                        it.requiredCorrectAnswers to it.excludeMasteredWordsFromPractice
                    }
                    .distinctUntilChanged()
            ) { words, (required, excludeMastered) ->
                Triple(required, excludeMastered, words)
            }.collect { (required, excludeMastered, words) ->
                val maxShortSessionWords = 15
                val requiredCoerced = required.coerceAtLeast(1)
                val cur = _state.value

                val practiceWords = wordsForPractice(
                    allWords = words,
                    requiredCorrectAnswers = requiredCoerced,
                    excludeMasteredWords = excludeMastered,
                    includeMasteredInSession = cur.includeMasteredInSession
                )

                // Update/reconcile in-memory metadata used for weighting.
                val nextReviewMeta = reconcileReviewMeta(
                    words = words,
                    requiredCorrectAnswers = requiredCoerced,
                    currentMeta = cur.reviewMetaByWordId
                )

                val hasPracticeWords = practiceWords.isNotEmpty()
                val nextSessionTarget = when {
                    cur.sessionTargetSelections > 0 -> cur.sessionTargetSelections
                    !hasPracticeWords -> 0
                    else -> practiceWords.size.coerceAtMost(maxShortSessionWords).coerceAtLeast(1)
                }

                val masteredCount = words.count { it.isLearnedAtThreshold(requiredCoerced) }
                val needsReviewCount = words.count { w ->
                    w.needsReview(
                        meta = nextReviewMeta[w.id],
                        requiredCorrectAnswers = requiredCoerced
                    )
                }

                val preserveCurrentWord =
                    cur.feedbackCorrect != null || cur.input.isNotBlank()

                val nextIndex: Int
                val nextLetters: List<String>
                if (preserveCurrentWord) {
                    nextIndex = cur.currentIndex
                    nextLetters = cur.letters
                } else {
                    val currentWordId = cur.words.getOrNull(cur.currentIndex)?.id
                    val currentWordStillExists =
                        currentWordId != null && practiceWords.any { it.id == currentWordId }

                    nextIndex = if (hasPracticeWords && currentWordStillExists) {
                        practiceWords.indexOfFirst { it.id == currentWordId }.coerceAtLeast(0)
                    } else if (hasPracticeWords) {
                        selectNextWord(
                            allWords = practiceWords,
                            requiredCorrectAnswers = requiredCoerced,
                            reviewMetaByWordId = nextReviewMeta,
                            lastWordId = null,
                            selectionStep = 0L
                        )?.let { chosen ->
                            practiceWords.indexOfFirst { it.id == chosen.id }.takeIf { it >= 0 }
                        } ?: 0
                    } else {
                        0
                    }

                    val nextWord = practiceWords.getOrNull(nextIndex)
                    nextLetters = nextWord?.let { shuffleLetters(it.text) }.orEmpty()
                }

                val nextSessionComplete =
                    if (!hasPracticeWords) {
                        false
                    } else {
                        cur.sessionComplete ||
                            (cur.sessionTargetSelections > 0 &&
                                cur.selectionStep >= cur.sessionTargetSelections)
                    }

                _state.update { s ->
                    val preserve = s.feedbackCorrect != null || s.input.isNotBlank()
                    s.copy(
                        allWords = words,
                        words = practiceWords,
                        excludeMasteredWords = excludeMastered,
                        currentIndex = if (preserve) {
                            s.currentIndex
                        } else {
                            nextIndex.coerceIn(0, (practiceWords.size - 1).coerceAtLeast(0))
                        },
                        letters = if (preserve) s.letters else nextLetters,
                        requiredCorrectAnswers = requiredCoerced,
                        reviewMetaByWordId = nextReviewMeta,
                        sessionTargetSelections = if (s.sessionTargetSelections > 0) {
                            s.sessionTargetSelections
                        } else {
                            nextSessionTarget
                        },
                        sessionComplete = nextSessionComplete,
                        masteredWordsCount = masteredCount,
                        wordsNeedingReviewCount = needsReviewCount
                    )
                }
            }
        }
    }

    fun showHints() {
        _state.update { cur ->
            if (!cur.hintsEnabled) cur.copy(showHints = false) else cur.copy(showHints = true)
        }
    }

    fun listen() {
        val w = currentWord() ?: return
        tts.speak(w.text)
    }

    fun playAnswerSuccessSound() {
        viewModelScope.launch { sound.playSuccess() }
    }

    fun playAnswerRetrySound() {
        viewModelScope.launch { sound.playRetry() }
    }

    fun onInputChange(value: String) {
        _state.update { it.copy(input = value) }
    }

    fun appendLetter(ch: String) {
        _state.update { it.copy(input = it.input + ch) }
    }

    fun clearAnimationHint() {
        _state.update { it.copy(animationHint = PracticeAnimHint.None, wordJustMastered = false) }
    }

    fun clearFeedback() {
        _state.update {
            it.copy(feedbackCorrect = null, spellingFeedback = null)
        }
    }

    fun checkWord() {
        if (blockWriteOperation()) return
        val w = currentWord() ?: return
        if (_state.value.feedbackCorrect != null) return

        viewModelScope.launch {
            if (!checkWordMutex.tryLock()) return@launch
            try {
                if (_state.value.feedbackCorrect != null) return@launch
                if (!isSessionAuthorizedForWrites()) {
                    blockWriteOperation()
                    return@launch
                }

                val settings = observeSettingsUseCase().first()
                val required = settings.requiredCorrectAnswers.coerceAtLeast(1)
                val attempt = _state.value.input
                val spellingFeedback = SpellingComparer.compare(attempt, w.text)
                val result = processSpelling(
                    w,
                    attempt,
                    required,
                    settings.mistakeBehavior
                )
                val updatedWord = result.updatedWord
                val justMastered =
                    result.isSpellingCorrect && w.masteredAt == null && updatedWord.masteredAt != null
                val masteredNow = updatedWord.isLearnedAtThreshold(required)
                val now = System.currentTimeMillis()

                val updatedAllWords = _state.value.allWords.toMutableList().also { list ->
                    val idx = list.indexOfFirst { it.id == updatedWord.id }
                    if (idx >= 0) list[idx] = updatedWord
                }
                val updatedPracticeWords = _state.value.words.map { w ->
                    if (w.id == updatedWord.id) updatedWord else w
                }

                val nextReviewMeta = _state.value.reviewMetaByWordId.toMutableMap().also { meta ->
                    val curMeta = meta[updatedWord.id] ?: PracticeWordReviewMeta(wordId = updatedWord.id)
                    meta[updatedWord.id] = curMeta.copy(
                        incorrectAttempts = updatedWord.incorrectCount,
                        lastSeenTimestamp = now,
                        needsReview = if (result.isSpellingCorrect) {
                            if (masteredNow) false else curMeta.needsReview
                        } else {
                            true
                        }
                    )
                }

                val masteredCount = updatedAllWords.count { it.isLearnedAtThreshold(required) }
                val needsReviewCount = updatedAllWords.count { word0 ->
                    word0.needsReview(
                        meta = nextReviewMeta[word0.id],
                        requiredCorrectAnswers = required
                    )
                }

                if (result.isSpellingCorrect) {
                    val before = rewardRepository.rewardState.first()
                    val isFirstEver = before.totalCorrectLifetime == 0
                    sessionBadges += rewardRepository.onCorrectAnswer(isFirstEver)
                    val newSessionCorrect = _state.value.sessionCorrect + 1

                    _state.update {
                        it.copy(
                            sessionCorrect = newSessionCorrect,
                            feedbackCorrect = true,
                            spellingFeedback = spellingFeedback,
                            animationHint = if (settings.animationsEnabled) PracticeAnimHint.BounceOk else PracticeAnimHint.None,
                            allWords = updatedAllWords,
                            words = updatedPracticeWords,
                            reviewMetaByWordId = nextReviewMeta,
                            masteredWordsCount = masteredCount,
                            wordsNeedingReviewCount = needsReviewCount,
                            wordJustMastered = justMastered
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            incorrectSubmissions = it.incorrectSubmissions + 1,
                            feedbackCorrect = false,
                            spellingFeedback = spellingFeedback,
                            animationHint = if (settings.animationsEnabled) PracticeAnimHint.ShakeWrong else PracticeAnimHint.None,
                            input = "",
                            wordJustMastered = false,
                            allWords = updatedAllWords,
                            words = updatedPracticeWords,
                            reviewMetaByWordId = nextReviewMeta,
                            masteredWordsCount = masteredCount,
                            wordsNeedingReviewCount = needsReviewCount
                        )
                    }
                }
            } finally {
                checkWordMutex.unlock()
            }
        }
    }

    fun onNextWord() {
        if (blockWriteOperation()) return
        val cur = _state.value

        if (cur.sessionComplete) return

        // Always reset UI flags/input when user explicitly proceeds.
        _state.update {
            it.copy(
                feedbackCorrect = null,
                spellingFeedback = null,
                input = "",
                showHints = false,
                wordJustMastered = false
            )
        }

        val words = cur.words
        if (words.isEmpty()) {
            viewModelScope.launch {
                completeSessionIfNeeded(finalCorrect = cur.sessionCorrect)
                _state.update { it.copy(sessionComplete = true) }
            }
            return
        }

        val nextStep = cur.selectionStep + 1
        val target = cur.sessionTargetSelections.takeIf { it > 0 } ?: words.size

        if (nextStep >= target) {
            viewModelScope.launch {
                completeSessionIfNeeded(finalCorrect = cur.sessionCorrect)
                _state.update {
                    it.copy(
                        sessionComplete = true,
                        selectionStep = nextStep,
                        feedbackCorrect = null,
                        spellingFeedback = null,
                        input = "",
                        showHints = false,
                        wordJustMastered = false
                    )
                }
            }
            return
        }

        val currentWordId = words.getOrNull(cur.currentIndex)?.id
        val nextWord = selectNextWord(
            allWords = words,
            requiredCorrectAnswers = cur.requiredCorrectAnswers,
            reviewMetaByWordId = cur.reviewMetaByWordId,
            lastWordId = currentWordId ?: cur.lastWordId,
            selectionStep = nextStep
        ) ?: words.first()

        val nextIndex = words.indexOfFirst { it.id == nextWord.id }.takeIf { it >= 0 } ?: 0

        val now = System.currentTimeMillis()
        _state.update {
            val meta = it.reviewMetaByWordId.toMutableMap().also { m ->
                val curMeta = m[nextWord.id] ?: PracticeWordReviewMeta(wordId = nextWord.id)
                // Mark this word as "seen" to avoid repeating it too quickly.
                m[nextWord.id] = curMeta.copy(
                    lastSeenTimestamp = now,
                    incorrectAttempts = nextWord.incorrectCount
                )
            }

            it.copy(
                currentIndex = nextIndex,
                letters = shuffleLetters(nextWord.text),
                lastWordId = nextWord.id,
                selectionStep = nextStep,
                reviewMetaByWordId = meta
            )
        }
        listen()
    }

    fun practiceAgain() {
        if (blockWriteOperation()) return
        val cur = _state.value
        val words = wordsForPractice(
            allWords = cur.allWords,
            requiredCorrectAnswers = cur.requiredCorrectAnswers,
            excludeMasteredWords = cur.excludeMasteredWords,
            includeMasteredInSession = false
        )
        if (words.isEmpty()) {
            if (cur.allWords.isNotEmpty()) {
                practiceMasteredWords()
            }
            return
        }

        isSessionFinalized = false
        val now = System.currentTimeMillis()
        val required = _state.value.requiredCorrectAnswers.coerceAtLeast(1)

        // Start fresh recency + needsReview, but keep historical incorrectAttempts (from persisted Word state).
        val resetMeta = words.associate { w ->
            w.id to PracticeWordReviewMeta(
                wordId = w.id,
                incorrectAttempts = w.incorrectCount,
                lastSeenTimestamp = 0L,
                    needsReview = w.incorrectCount > 0 && !w.isLearnedAtThreshold(required)
            )
        }

        val target = words.size.coerceAtMost(15).coerceAtLeast(1)
        val firstWord = selectNextWord(
            allWords = words,
            requiredCorrectAnswers = required,
            reviewMetaByWordId = resetMeta,
            lastWordId = null,
            selectionStep = 0L
        ) ?: words.first()

        val firstIndex = words.indexOfFirst { it.id == firstWord.id }.takeIf { it >= 0 } ?: 0
        val seededMeta = resetMeta.toMutableMap().also { m ->
            val curMeta = m[firstWord.id] ?: PracticeWordReviewMeta(wordId = firstWord.id)
            m[firstWord.id] = curMeta.copy(lastSeenTimestamp = now)
        }

        _state.update {
            it.copy(
                includeMasteredInSession = false,
                words = words,
                sessionCorrect = 0,
                incorrectSubmissions = 0,
                feedbackCorrect = null,
                spellingFeedback = null,
                animationHint = PracticeAnimHint.None,
                input = "",
                showHints = false,
                wordJustMastered = false,
                selectionStep = 0L,
                lastWordId = firstWord.id,
                sessionTargetSelections = target,
                sessionComplete = false,
                currentIndex = firstIndex,
                letters = shuffleLetters(firstWord.text),
                reviewMetaByWordId = seededMeta,
                masteredWordsCount = it.allWords.count { w -> w.isLearnedAtThreshold(required) },
                wordsNeedingReviewCount = it.allWords.count { w ->
                    w.needsReview(
                        meta = seededMeta[w.id],
                        requiredCorrectAnswers = required
                    )
                }
            )
        }

        listen()
    }

    /** Start a session that includes mastered words (ignores exclude-mastered for this session). */
    fun practiceMasteredWords() {
        if (blockWriteOperation()) return
        val cur = _state.value
        val allWords = cur.allWords
        if (allWords.isEmpty()) return

        isSessionFinalized = false
        val now = System.currentTimeMillis()
        val required = cur.requiredCorrectAnswers.coerceAtLeast(1)

        val resetMeta = allWords.associate { w ->
            w.id to PracticeWordReviewMeta(
                wordId = w.id,
                incorrectAttempts = w.incorrectCount,
                lastSeenTimestamp = 0L,
                needsReview = w.incorrectCount > 0 && !w.isLearnedAtThreshold(required)
            )
        }

        val target = allWords.size.coerceAtMost(15).coerceAtLeast(1)
        val firstWord = selectNextWord(
            allWords = allWords,
            requiredCorrectAnswers = required,
            reviewMetaByWordId = resetMeta,
            lastWordId = null,
            selectionStep = 0L
        ) ?: allWords.first()

        val firstIndex = allWords.indexOfFirst { it.id == firstWord.id }.takeIf { it >= 0 } ?: 0
        val seededMeta = resetMeta.toMutableMap().also { m ->
            val curMeta = m[firstWord.id] ?: PracticeWordReviewMeta(wordId = firstWord.id)
            m[firstWord.id] = curMeta.copy(lastSeenTimestamp = now)
        }

        _state.update {
            it.copy(
                includeMasteredInSession = true,
                words = allWords,
                sessionCorrect = 0,
                incorrectSubmissions = 0,
                feedbackCorrect = null,
                spellingFeedback = null,
                animationHint = PracticeAnimHint.None,
                input = "",
                showHints = false,
                wordJustMastered = false,
                selectionStep = 0L,
                lastWordId = firstWord.id,
                sessionTargetSelections = target,
                sessionComplete = false,
                currentIndex = firstIndex,
                letters = shuffleLetters(firstWord.text),
                reviewMetaByWordId = seededMeta,
                masteredWordsCount = allWords.count { w -> w.isLearnedAtThreshold(required) },
                wordsNeedingReviewCount = allWords.count { w ->
                    w.needsReview(
                        meta = seededMeta[w.id],
                        requiredCorrectAnswers = required
                    )
                }
            )
        }

        listen()
    }

    fun resetListProgress() {
        if (blockWriteOperation()) return
        viewModelScope.launch {
            wordRepository.resetProgress(listId)
        }
    }

    private suspend fun finishSession(finalCorrect: Int) {
        if (!isSessionAuthorizedForWrites()) return
        val s = _state.value
        val total = s.allWords.size
        if (total == 0) {
            return
        }
        val perfect = s.incorrectSubmissions == 0
        sessionBadges += rewardRepository.onSessionCompleted(perfect, total)
        val listName = wordRepository.getWordListName(listId).orEmpty()
        practiceResultBuffer.set(
            PracticeResult(
                listId = listId,
                listName = listName,
                correct = finalCorrect.coerceAtMost(total),
                total = total,
                starsEarned = finalCorrect,
                newBadges = sessionBadges.distinct(),
                mistakeWordIds = emptyList()
            )
        )
        sound.playCompletion()
    }

    private suspend fun finishSession() {
        finishSession(_state.value.sessionCorrect)
    }

    private suspend fun finishSessionAndEmit() {
        finishSession()
        _events.emit(PracticeEvent.Finished)
    }

    private fun completeSessionIfNeeded(finalCorrect: Int) {
        if (!isSessionAuthorizedForWrites()) return
        if (isSessionFinalized) return
        isSessionFinalized = true
        viewModelScope.launch { finishSession(finalCorrect) }
    }

    private fun currentWord(): Word? =
        _state.value.words.getOrNull(_state.value.currentIndex)

    /**
     * Pick the next practice word using a lightweight weighted rotation.
     *
     * Goals:
     * - Prefer harder words (lower correctCount) so they repeat more often.
     * - Still allow mastered words to appear rarely.
     * - Avoid showing the exact same word twice in a row when alternatives exist.
     * - Avoid repeating the same word too soon (recency penalty).
     */
    private fun selectNextWord(
        allWords: List<Word>,
        requiredCorrectAnswers: Int,
        reviewMetaByWordId: Map<Long, PracticeWordReviewMeta>,
        lastWordId: Long?,
        selectionStep: Long
    ): Word? {
        if (allWords.isEmpty()) return null
        if (allWords.size == 1) return allWords.first()

        val required = requiredCorrectAnswers.coerceAtLeast(1)

        val candidates = if (lastWordId != null) {
            allWords.filterNot { it.id == lastWordId }.ifEmpty { allWords }
        } else {
            allWords
        }

        val now = System.currentTimeMillis()
        val weights = candidates.map { w ->
            calculateWordWeight(
                word = w,
                requiredCorrectAnswers = required,
                meta = reviewMetaByWordId[w.id],
                now = now
            )
        }

        val totalWeight = weights.sum().coerceAtLeast(0.0001f)
        val seed = (listId * 31L) + (selectionStep * 1103515245L) + (lastWordId ?: 0L)
        val r = Random(seed)
        val target = r.nextDouble() * totalWeight.toDouble()

        var acc = 0.0
        for (i in candidates.indices) {
            acc += weights[i].toDouble()
            if (target <= acc) return candidates[i]
        }
        return candidates.last()
    }

    private fun calculateWordWeight(
        word: Word,
        requiredCorrectAnswers: Int,
        meta: PracticeWordReviewMeta?,
        now: Long
    ): Float {
        val required = requiredCorrectAnswers.coerceAtLeast(1)

        // Persisted mastery overrides the live threshold; otherwise progress is derived from counts.
        val level = word.masteryLevel(required)
        val base = when (level) {
            WordMasteryLevel.NEW -> 22f
            WordMasteryLevel.LEARNING -> 15f
            WordMasteryLevel.FAMILIAR -> 8f
            WordMasteryLevel.MASTERED -> 2.2f
        }

        val incorrectAttempts = meta?.incorrectAttempts ?: word.incorrectCount
        val incorrectBoost = 1f + incorrectAttempts.toFloat() * 0.25f

        // Prioritize lower correctCount until mastery (persisted or by threshold).
        val remaining =
            if (word.isLearnedAtThreshold(required)) 0
            else (required - word.correctCount).coerceAtLeast(0)
        val urgency = (remaining.toFloat() / required.toFloat()).coerceIn(0f, 1f)

        val reviewBoost = if (meta?.needsReview == true) 2.4f else 1f

        // Avoid repeating the same word too soon.
        val lastSeen = meta?.lastSeenTimestamp ?: 0L
        val recentCooldownMs = 5_000L
        val recencyMultiplier =
            if (lastSeen > 0L && (now - lastSeen) < recentCooldownMs) 0.25f else 1f

        val weight = base *
            incorrectBoost *
            (0.85f + urgency * 0.65f) *
            reviewBoost *
            recencyMultiplier

        return weight.coerceAtLeast(0.01f)
    }

    private fun reconcileReviewMeta(
        words: List<Word>,
        requiredCorrectAnswers: Int,
        currentMeta: Map<Long, PracticeWordReviewMeta>
    ): Map<Long, PracticeWordReviewMeta> {
        if (words.isEmpty()) return emptyMap()

        return words.associate { w ->
            val existing = currentMeta[w.id]
            val isMastered = w.isLearnedAtThreshold(requiredCorrectAnswers)

            w.id to (existing?.copy(
                incorrectAttempts = w.incorrectCount,
                needsReview = if (isMastered) {
                    false
                } else {
                    existing.needsReview || w.incorrectCount > 0
                }
            )
                ?: PracticeWordReviewMeta(
                    wordId = w.id,
                    incorrectAttempts = w.incorrectCount,
                    lastSeenTimestamp = 0L,
                    needsReview = w.incorrectCount > 0 && !isMastered
                ))
        }
    }

    private fun Word.masteryLevel(requiredCorrectAnswers: Int): WordMasteryLevel {
        val required = requiredCorrectAnswers.coerceAtLeast(1)
        if (isLearnedAtThreshold(required)) return WordMasteryLevel.MASTERED
        val correct = correctCount.coerceAtLeast(0)

        return when {
            correct == 0 -> WordMasteryLevel.NEW
            correct.toFloat() / required.toFloat() < 0.5f -> WordMasteryLevel.LEARNING
            else -> WordMasteryLevel.FAMILIAR
        }
    }

    private fun Word.needsReview(
        meta: PracticeWordReviewMeta?,
        requiredCorrectAnswers: Int
    ): Boolean = meta?.needsReview == true && !isLearnedAtThreshold(requiredCorrectAnswers)

    private fun wordsForPractice(
        allWords: List<Word>,
        requiredCorrectAnswers: Int,
        excludeMasteredWords: Boolean,
        includeMasteredInSession: Boolean
    ): List<Word> {
        if (includeMasteredInSession || !excludeMasteredWords) return allWords
        val required = requiredCorrectAnswers.coerceAtLeast(1)
        return allWords.filterNot { it.isLearnedAtThreshold(required) }
    }

    private fun shuffleLetters(text: String): List<String> {
        val lettersOnly = text.filter { it.isLetter() }.map { it.toString() }
        return if (lettersOnly.isEmpty()) {
            text.map { it.toString() }.shuffled()
        } else {
            lettersOnly.shuffled()
        }
    }
}
