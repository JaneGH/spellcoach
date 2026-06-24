package com.itclimb.spellcoach.feature.wordlists.presentation

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.itclimb.spellcoach.domain.usecase.ObserveWordListsUseCase
import com.itclimb.spellcoach.feature.practice.PracticeListHolder
import com.itclimb.spellcoach.testing.FakeLastPracticeListStore
import com.itclimb.spellcoach.testing.FakeWordRepository
import com.itclimb.spellcoach.testing.MainDispatcherRule
import com.itclimb.spellcoach.testing.WordListFixtures
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
        store: FakeLastPracticeListStore = FakeLastPracticeListStore(),
        practiceListHolder: PracticeListHolder = PracticeListHolder(store),
    ): Triple<WordListsViewModel, FakeWordRepository, FakeLastPracticeListStore> {
        return Triple(
            WordListsViewModel(
                observeWordLists = ObserveWordListsUseCase(repository),
                wordRepository = repository,
                practiceListHolder = practiceListHolder
            ),
            repository,
            store
        )
    }

    @Test
    fun uiState_startsLoading_thenEmitsLists() = runTest {
        val (viewModel, repository, _) = createViewModel()

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
    fun rememberPracticeList_persistsListId() = runTest {
        val (viewModel, _, store) = createViewModel()

        viewModel.rememberPracticeList(42L)
        advanceUntilIdle()

        assertThat(store.lastPracticeListId.first()).isEqualTo(42L)
    }

    @Test
    fun rememberPracticeList_survivesViewModelRecreation() = runTest {
        val store = FakeLastPracticeListStore()
        val repository = FakeWordRepository()
        val holder = PracticeListHolder(store)
        val viewModel1 = WordListsViewModel(
            observeWordLists = ObserveWordListsUseCase(repository),
            wordRepository = repository,
            practiceListHolder = holder
        )

        viewModel1.rememberPracticeList(42L)
        advanceUntilIdle()

        val viewModel2 = WordListsViewModel(
            observeWordLists = ObserveWordListsUseCase(repository),
            wordRepository = repository,
            practiceListHolder = PracticeListHolder(store)
        )

        assertThat(viewModel2).isNotNull()
        assertThat(store.lastPracticeListId.first()).isEqualTo(42L)
    }

    @Test
    fun resetListProgress_delegatesToRepository() = runTest {
        val (viewModel, repository, _) = createViewModel()

        viewModel.resetListProgress(7L)
        advanceUntilIdle()

        assertThat(repository.resetProgressCalls).containsExactly(7L)
    }

    @Test
    fun deleteList_removesFromRepositoryAndClearsPracticeHolderWhenMatching() = runTest {
        val store = FakeLastPracticeListStore(initial = 3L)
        val (viewModel, repository) = createViewModel(store = store)
        repository.setLists(listOf(WordListFixtures.sampleList(id = 3L)))

        viewModel.deleteList(3L)
        advanceUntilIdle()

        assertThat(repository.deleteListCalls).containsExactly(3L)
        assertThat(store.lastPracticeListId.first()).isNull()
    }

    @Test
    fun deleteList_keepsPracticeHolderWhenDifferentList() = runTest {
        val store = FakeLastPracticeListStore(initial = 99L)
        val (viewModel, _) = createViewModel(store = store)

        viewModel.deleteList(3L)
        advanceUntilIdle()

        assertThat(store.lastPracticeListId.first()).isEqualTo(99L)
    }

    @Test
    fun deleteList_clearsPendingPracticeListIdWhenMatching() = runTest {
        val holder = PracticeListHolder(FakeLastPracticeListStore()).apply {
            pendingPracticeListId = 3L
        }
        val (viewModel, repository) = createViewModel(practiceListHolder = holder)
        repository.setLists(listOf(WordListFixtures.sampleList(id = 3L)))

        viewModel.deleteList(3L)
        advanceUntilIdle()

        assertThat(repository.deleteListCalls).containsExactly(3L)
        assertThat(holder.pendingPracticeListId).isNull()
    }

    @Test
    fun deleteList_keepsPendingPracticeListIdWhenDifferentList() = runTest {
        val holder = PracticeListHolder(FakeLastPracticeListStore()).apply {
            pendingPracticeListId = 99L
        }
        val (viewModel, _) = createViewModel(practiceListHolder = holder)

        viewModel.deleteList(3L)
        advanceUntilIdle()

        assertThat(holder.pendingPracticeListId).isEqualTo(99L)
    }

    @Test
    fun uiState_reflectsEmptyListAfterDelete() = runTest {
        val (viewModel, repository, _) = createViewModel()
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
