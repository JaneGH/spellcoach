package com.itclimb.spellcoach.feature.practice.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.itclimb.spellcoach.domain.model.Badge
import com.itclimb.spellcoach.domain.model.PracticeResult
import com.itclimb.spellcoach.domain.model.RewardState
import com.itclimb.spellcoach.domain.model.Word
import com.itclimb.spellcoach.domain.practice.PracticeResultBuffer
import com.itclimb.spellcoach.domain.repository.RewardRepository
import com.itclimb.spellcoach.domain.speech.RewardSoundPlayer
import com.itclimb.spellcoach.domain.usecase.ObserveSettingsUseCase
import com.itclimb.spellcoach.domain.usecase.ProcessSpellingResultUseCase
import com.itclimb.spellcoach.feature.practice.PracticeListHolder
import com.itclimb.spellcoach.testing.FakeLastPracticeListStore
import com.itclimb.spellcoach.testing.FakeSettingsRepository
import com.itclimb.spellcoach.testing.FakeSpellCoachTextToSpeech
import com.itclimb.spellcoach.testing.FakeWordRepository
import com.itclimb.spellcoach.testing.MainDispatcherRule
import com.itclimb.spellcoach.testing.WordListFixtures
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PracticeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val listA = 1L
    private val listB = 2L

    private class NoOpRewardRepository : RewardRepository {
        override val rewardState = MutableStateFlow(
            RewardState(
                totalCorrectLifetime = 0,
                currentStreak = 0,
                longestStreak = 0,
                lastPracticeDayEpochDay = 0L,
                unlockedBadges = emptySet(),
            )
        )

        override suspend fun onCorrectAnswer(isFirstLifetimeCorrect: Boolean): List<Badge> =
            emptyList()

        override suspend fun onSessionCompleted(allCorrect: Boolean, totalWords: Int): List<Badge> =
            emptyList()
    }

    private class NoOpPracticeResultBuffer : PracticeResultBuffer {
        override fun set(result: PracticeResult) = Unit
        override fun consume(): PracticeResult? = null
        override fun peek(): PracticeResult? = null
    }

    private class NoOpRewardSoundPlayer : RewardSoundPlayer {
        override suspend fun playSuccess() = Unit
        override suspend fun playRetry() = Unit
        override suspend fun playCompletion() = Unit
    }

    private fun repoWithWords(listId: Long): FakeWordRepository {
        return FakeWordRepository(
            initialLists = listOf(WordListFixtures.sampleList(id = listId))
        ).apply {
            setWordsForList(
                listId,
                listOf(
                    Word(
                        id = 10L,
                        listId = listId,
                        text = "cat",
                        correctCount = 0,
                        incorrectCount = 0,
                        isMastered = false,
                    )
                )
            )
        }
    }

    private fun createViewModel(
        listId: Long,
        savedStateHandle: SavedStateHandle = SavedStateHandle(mapOf("listId" to listId)),
        repo: FakeWordRepository = repoWithWords(listId),
        practiceListHolder: PracticeListHolder = PracticeListHolder(FakeLastPracticeListStore()),
    ): PracticeViewModel {
        return createViewModel(
            listId = listId,
            savedStateHandle = savedStateHandle,
            repo = repo,
            practiceListHolder = practiceListHolder,
            processSpelling = ProcessSpellingResultUseCase(repo),
        )
    }

    private fun createViewModel(
        listId: Long,
        savedStateHandle: SavedStateHandle,
        repo: FakeWordRepository,
        practiceListHolder: PracticeListHolder,
        processSpelling: ProcessSpellingResultUseCase,
    ): PracticeViewModel {
        return PracticeViewModel(
            savedStateHandle = savedStateHandle,
            wordRepository = repo,
            processSpelling = processSpelling,
            observeSettingsUseCase = ObserveSettingsUseCase(FakeSettingsRepository()),
            rewardRepository = NoOpRewardRepository(),
            practiceResultBuffer = NoOpPracticeResultBuffer(),
            sound = NoOpRewardSoundPlayer(),
            tts = FakeSpellCoachTextToSpeech(),
            practiceListHolder = practiceListHolder,
        )
    }

    private fun authorizedHolder(listId: Long) =
        PracticeListHolder(FakeLastPracticeListStore()).apply { pendingPracticeListId = listId }

    @Test
    fun invalidListId_exposesErrorStateAndSkipsListRepositoryCalls() = runTest {
        val repo = repoWithWords(listA)

        val vm = createViewModel(
            listId = 0L,
            savedStateHandle = SavedStateHandle(),
            repo = repo,
        )

        advanceUntilIdle()

        assertThat(vm.state.value.listIdValid).isFalse()
        assertThat(vm.state.value.loading).isFalse()
        assertThat(vm.state.value.words).isEmpty()
        assertThat(repo.getWordListNameCalls).isEmpty()
        assertThat(repo.observeWordsForListCalls).isEmpty()

        vm.resetListProgress()
        advanceUntilIdle()
        assertThat(repo.resetProgressCalls).isEmpty()
    }

    @Test
    fun validListId_loadsWordsForRouteListId() = runTest {
        val repo = repoWithWords(listA)

        val vm = createViewModel(listId = listA, repo = repo)

        advanceUntilIdle()

        assertThat(vm.state.value.listIdValid).isTrue()
        assertThat(vm.state.value.listId).isEqualTo(listA)
        assertThat(vm.state.value.words).hasSize(1)
        assertThat(repo.getWordListNameCalls).containsExactly(listA)
        assertThat(repo.observeWordsForListCalls).containsExactly(listA)
    }

    @Test
    fun pendingDifferentList_blocksSessionAndSkipsRepositoryLoads() = runTest {
        val repo = repoWithWords(listA)
        val holder = PracticeListHolder(FakeLastPracticeListStore()).apply { pendingPracticeListId = listB }

        val vm = createViewModel(
            listId = listA,
            repo = repo,
            practiceListHolder = holder,
        )

        advanceUntilIdle()

        assertThat(vm.state.value.sessionWriteBlocked).isTrue()
        assertThat(vm.state.value.loading).isFalse()
        assertThat(repo.observeWordsForListCalls).isEmpty()
        assertThat(repo.getWordListNameCalls).isEmpty()
    }

    @Test
    fun pendingDifferentList_checkWordDoesNotWriteProgress() = runTest {
        val repo = repoWithWords(listA)
        val holder = PracticeListHolder(FakeLastPracticeListStore()).apply { pendingPracticeListId = listB }

        val vm = createViewModel(
            listId = listA,
            repo = repo,
            practiceListHolder = holder,
        )

        advanceUntilIdle()
        vm.onInputChange("cat")
        vm.checkWord()
        advanceUntilIdle()

        assertThat(repo.updateWordCalls).isEmpty()
        assertThat(vm.state.value.sessionWriteBlocked).isTrue()
    }

    @Test
    fun pendingDifferentList_checkWord_emitsStaleSessionBlockedEvent() = runTest {
        val repo = repoWithWords(listA)
        val holder = PracticeListHolder(FakeLastPracticeListStore()).apply { pendingPracticeListId = listB }

        val vm = createViewModel(
            listId = listA,
            repo = repo,
            practiceListHolder = holder,
        )

        vm.events.test {
            advanceUntilIdle()
            vm.checkWord()
            advanceUntilIdle()
            assertThat(awaitItem()).isEqualTo(PracticeEvent.StaleSessionBlocked)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun pendingMatchingList_allowsWrites() = runTest {
        val repo = repoWithWords(listB)
        val holder = PracticeListHolder(FakeLastPracticeListStore()).apply { pendingPracticeListId = listB }

        val vm = createViewModel(
            listId = listB,
            repo = repo,
            practiceListHolder = holder,
        )

        advanceUntilIdle()
        vm.onInputChange("cat")
        vm.checkWord()
        advanceUntilIdle()

        assertThat(vm.state.value.sessionWriteBlocked).isFalse()
        assertThat(repo.updateWordCalls).hasSize(1)
        assertThat(repo.updateWordCalls.single().listId).isEqualTo(listB)
    }

    @Test
    fun checkWord_doubleTap_onlyOneCheckRunsAndSecondWaitsForFirst() = runTest {
        val updateGate = CompletableDeferred<Unit>()
        val repo = repoWithWords(listB).apply {
            beforeUpdateWord = { updateGate.await() }
        }
        val holder = authorizedHolder(listB)

        val vm = createViewModel(
            listId = listB,
            repo = repo,
            practiceListHolder = holder,
        )

        advanceUntilIdle()
        vm.onInputChange("cat")

        vm.checkWord()
        runCurrent()

        assertThat(vm.state.value.isCheckingWord).isTrue()
        assertThat(repo.updateWordCalls).isEmpty()

        vm.checkWord()
        runCurrent()

        assertThat(repo.updateWordCalls).isEmpty()

        updateGate.complete(Unit)
        advanceUntilIdle()

        assertThat(repo.updateWordCalls).hasSize(1)
        assertThat(vm.state.value.feedbackCorrect).isTrue()
        assertThat(vm.state.value.isCheckingWord).isFalse()
    }

    @Test
    fun checkWord_setsCheckingFlagDuringProcessing() = runTest {
        val updateGate = CompletableDeferred<Unit>()
        val repo = repoWithWords(listB).apply {
            beforeUpdateWord = { updateGate.await() }
        }
        val holder = authorizedHolder(listB)

        val vm = createViewModel(
            listId = listB,
            repo = repo,
            practiceListHolder = holder,
        )

        advanceUntilIdle()
        vm.onInputChange("cat")
        vm.checkWord()
        runCurrent()

        assertThat(vm.state.value.isCheckingWord).isTrue()

        updateGate.complete(Unit)
        advanceUntilIdle()

        assertThat(vm.state.value.isCheckingWord).isFalse()
    }

    @Test
    fun checkWord_releasesCheckingStateAfterError_allowingRetry() = runTest {
        var failNextUpdate = true
        val repo = repoWithWords(listB).apply {
            beforeUpdateWord = {
                if (failNextUpdate) {
                    failNextUpdate = false
                    error("update failed")
                }
            }
        }
        val holder = authorizedHolder(listB)

        val vm = createViewModel(
            listId = listB,
            repo = repo,
            practiceListHolder = holder,
        )

        advanceUntilIdle()
        vm.onInputChange("cat")

        vm.checkWord()
        advanceUntilIdle()

        assertThat(vm.state.value.isCheckingWord).isFalse()
        assertThat(vm.state.value.feedbackCorrect).isNull()
        assertThat(repo.updateWordCalls).isEmpty()

        vm.checkWord()
        advanceUntilIdle()

        assertThat(vm.state.value.isCheckingWord).isFalse()
        assertThat(repo.updateWordCalls).hasSize(1)
        assertThat(vm.state.value.feedbackCorrect).isTrue()
    }
}
