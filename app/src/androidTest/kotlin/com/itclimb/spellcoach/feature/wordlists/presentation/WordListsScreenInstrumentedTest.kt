package com.itclimb.spellcoach.feature.wordlists.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.itclimb.spellcoach.core.designsystem.theme.SpellCoachTheme
import com.itclimb.spellcoach.domain.usecase.ObserveWordListsUseCase
import com.itclimb.spellcoach.feature.practice.PracticeListHolder
import com.itclimb.spellcoach.testing.FakeWordRepository
import com.itclimb.spellcoach.testing.WordListFixtures
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WordListsScreenInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun emptyState_showsEmptyMessageAndCreateButton() {
        val viewModel = createViewModel()

        var createClicked = false
        composeTestRule.setContent {
            SpellCoachTheme(darkTheme = false) {
                WordListsScreen(
                    onCreateNewList = { createClicked = true },
                    onPracticeList = {},
                    onEditList = {},
                    onManageWords = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("No lists yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create a list").performClick()
        assertTrue(createClicked)
    }

    @Test
    fun populatedState_showsListNamesAndFab() {
        val animals = WordListFixtures.sampleList(name = "Animals")
        val viewModel = createViewModel(initialLists = listOf(animals))

        composeTestRule.setContent {
            SpellCoachTheme(darkTheme = false) {
                WordListsScreen(
                    onCreateNewList = {},
                    onPracticeList = {},
                    onEditList = {},
                    onManageWords = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Animals").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Create new word list").assertIsDisplayed()
    }

    @Test
    fun clickingListCard_startsPracticeForThatList() {
        val animals = WordListFixtures.sampleList(id = 5L, name = "Animals")
        val viewModel = createViewModel(initialLists = listOf(animals))

        var practicedListId: Long? = null
        composeTestRule.setContent {
            SpellCoachTheme(darkTheme = false) {
                WordListsScreen(
                    onCreateNewList = {},
                    onPracticeList = { practicedListId = it },
                    onEditList = {},
                    onManageWords = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Animals").performClick()
        assertEquals(5L, practicedListId)
    }

    private fun createViewModel(
        initialLists: List<com.itclimb.spellcoach.domain.model.WordList> = emptyList()
    ): WordListsViewModel {
        val repository = FakeWordRepository(initialLists)
        return WordListsViewModel(
            observeWordLists = ObserveWordListsUseCase(repository),
            wordRepository = repository,
            practiceListHolder = PracticeListHolder()
        )
    }
}
