package com.itclimb.spellcoach.feature.wordlists.presentation

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.itclimb.spellcoach.domain.usecase.ObserveWordListsUseCase
import com.itclimb.spellcoach.feature.practice.PracticeListHolder
import com.itclimb.spellcoach.testing.FakeWordRepository
import com.itclimb.spellcoach.testing.MainDispatcherRule
import com.itclimb.spellcoach.testing.WordListFixtures
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WordListsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun createViewModel(
        repository: FakeWordRepository = FakeWordRepository(),
        practiceListHolder: PracticeListHolder = PracticeListHolder(),
    ): Pair<WordListsViewModel, FakeWordRepository> {
        return WordListsViewModel(
            observeWordLists = ObserveWordListsUseCase(repository),
            wordRepository = repository,
            practiceListHolder = practiceListHolder
        ) to repository
    }

    @Test
    fun uiState_startsLoading_thenEmitsLists() = runTest {
        val (viewModel, repository) = createViewModel()

        viewModel.uiState.test {
            assertThat(awaitItem().loading).isTrue()

            val animals = WordListFixtures.sampleList()
            repository.setLists(listOf(animals))
            advanceUntilIdle()

            val loaded = awaitItem()
            assertThat(loaded.loading).isFalse()
            assertThat(loaded.lists).containsExactly(animals)
        }
    }

    @Test
    fun rememberPracticeList_storesListIdInHolder() {
        val holder = PracticeListHolder()
        val (viewModel, _) = createViewModel(practiceListHolder = holder)

        viewModel.rememberPracticeList(42L)

        assertThat(holder.lastListId).isEqualTo(42L)
    }

    @Test
    fun resetListProgress_delegatesToRepository() = runTest {
        val (viewModel, repository) = createViewModel()

        viewModel.resetListProgress(7L)
        advanceUntilIdle()

        assertThat(repository.resetProgressCalls).containsExactly(7L)
    }

    @Test
    fun deleteList_removesFromRepositoryAndClearsPracticeHolderWhenMatching() = runTest {
        val holder = PracticeListHolder().apply { lastListId = 3L }
        val (viewModel, repository) = createViewModel(practiceListHolder = holder)
        repository.setLists(listOf(WordListFixtures.sampleList(id = 3L)))

        viewModel.deleteList(3L)
        advanceUntilIdle()

        assertThat(repository.deleteListCalls).containsExactly(3L)
        assertThat(holder.lastListId).isNull()
    }

    @Test
    fun deleteList_keepsPracticeHolderWhenDifferentList() = runTest {
        val holder = PracticeListHolder().apply { lastListId = 99L }
        val (viewModel, _) = createViewModel(practiceListHolder = holder)

        viewModel.deleteList(3L)
        advanceUntilIdle()

        assertThat(holder.lastListId).isEqualTo(99L)
    }

    @Test
    fun uiState_reflectsEmptyListAfterDelete() = runTest {
        val (viewModel, repository) = createViewModel()
        repository.setLists(listOf(WordListFixtures.sampleList(id = 1L)))

        viewModel.uiState.test {
            skipItems(2)

            viewModel.deleteList(1L)
            advanceUntilIdle()

            val afterDelete = awaitItem()
            assertThat(afterDelete.lists).isEmpty()
            assertThat(afterDelete.loading).isFalse()
        }
    }
}
