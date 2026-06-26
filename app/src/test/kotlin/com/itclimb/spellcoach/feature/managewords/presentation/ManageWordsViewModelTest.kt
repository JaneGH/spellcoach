package com.itclimb.spellcoach.feature.managewords.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.itclimb.spellcoach.domain.model.Word
import com.itclimb.spellcoach.domain.usecase.ObserveSettingsUseCase
import com.itclimb.spellcoach.domain.usecase.ObserveWordsForListUseCase
import com.itclimb.spellcoach.testing.FakeSettingsRepository
import com.itclimb.spellcoach.testing.FakeSpellCoachTextToSpeech
import com.itclimb.spellcoach.testing.FakeWordRepository
import com.itclimb.spellcoach.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ManageWordsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val listId = 1L

    private fun createViewModel(
        repo: FakeWordRepository = FakeWordRepository(),
        tts: FakeSpellCoachTextToSpeech = FakeSpellCoachTextToSpeech(),
    ): ManageWordsViewModel {
        return ManageWordsViewModel(
            savedStateHandle = SavedStateHandle(mapOf("listId" to listId)),
            observeWordsForList = ObserveWordsForListUseCase(repo),
            observeSettings = ObserveSettingsUseCase(FakeSettingsRepository()),
            wordRepository = repo,
            textToSpeech = tts,
        )
    }

    @Test
    fun setSearchQuery_updatesUiState() = runTest {
        val vm = createViewModel()

        vm.uiState.test {
            awaitItem()
            vm.setSearchQuery("cat")
            assertThat(awaitItem().searchQuery).isEqualTo("cat")
        }
    }

    @Test
    fun deleteWord_delegatesToRepository() = runTest {
        val repo = FakeWordRepository()
        val vm = createViewModel(repo = repo)

        vm.deleteWord(99L)
        advanceUntilIdle()

        assertThat(repo.deleteWordCalls).containsExactly(99L)
    }

    @Test
    fun speakWord_callsTextToSpeech() {
        val tts = FakeSpellCoachTextToSpeech()
        val vm = createViewModel(tts = tts)

        vm.speakWord("hello")

        assertThat(tts.lastSpokenText).isEqualTo("hello")
    }

    @Test
    fun renameWord_duplicate_emitsRenameDuplicate() = runTest {
        val repo = FakeWordRepository()
        repo.setWordsForList(
            listId,
            listOf(
                Word(id = 1L, listId = listId, text = "cat", correctCount = 0, incorrectCount = 0, isMastered = false),
                Word(id = 2L, listId = listId, text = "dog", correctCount = 0, incorrectCount = 0, isMastered = false),
            )
        )
        val vm = createViewModel(repo = repo)

        vm.events.test {
            vm.renameWord(wordId = 1L, newText = "dog")
            advanceUntilIdle()
            assertThat(awaitItem()).isEqualTo(ManageWordsEvent.RenameDuplicate)
        }
        assertThat(repo.updateWordCalls).isEmpty()
    }

    @Test
    fun renameWord_uniqueWord_emitsRenameSucceeded() = runTest {
        val repo = FakeWordRepository()
        val word = Word(id = 1L, listId = listId, text = "cat", correctCount = 0, incorrectCount = 0, isMastered = false)
        repo.setWordsForList(listId, listOf(word))
        val vm = createViewModel(repo = repo)

        vm.events.test {
            vm.renameWord(wordId = 1L, newText = "fox")
            advanceUntilIdle()
            assertThat(awaitItem()).isEqualTo(ManageWordsEvent.RenameSucceeded)
        }
        assertThat(repo.lastUpdatedWord?.text).isEqualTo("fox")
    }

    @Test
    fun renameWord_invalid_emitsRenameInvalid() = runTest {
        val repo = FakeWordRepository()
        val word = Word(id = 1L, listId = listId, text = "cat", correctCount = 0, incorrectCount = 0, isMastered = false)
        repo.setWordsForList(listId, listOf(word))
        val vm = createViewModel(repo = repo)

        vm.events.test {
            vm.renameWord(wordId = 1L, newText = "123")
            advanceUntilIdle()
            assertThat(awaitItem()).isEqualTo(ManageWordsEvent.RenameInvalid)
        }
        assertThat(repo.updateWordCalls).isEmpty()
    }

    @Test
    fun toggleMastered_marksWordAsMastered() = runTest {
        val repo = FakeWordRepository()
        val word = Word(
            id = 10L,
            listId = listId,
            text = "apple",
            correctCount = 0,
            incorrectCount = 0,
            isMastered = false
        )
        repo.setWordsForList(listId, listOf(word))
        val vm = createViewModel(repo = repo)

        vm.toggleMastered(word)
        advanceUntilIdle()

        assertThat(repo.lastUpdatedWord?.isMastered).isTrue()
    }
}
