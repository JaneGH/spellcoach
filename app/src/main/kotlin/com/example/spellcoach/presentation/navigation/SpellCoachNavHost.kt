package com.example.spellcoach.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.spellcoach.presentation.addwords.AddWordsScreen
import com.example.spellcoach.presentation.components.MainTab
import com.example.spellcoach.presentation.components.SpellCoachBottomBar
import com.example.spellcoach.presentation.practice.PracticeScreen
import com.example.spellcoach.presentation.results.ResultsScreen
import com.example.spellcoach.presentation.settings.SettingsScreen
import com.example.spellcoach.presentation.wordlists.WordListsScreen

@Composable
fun SpellCoachNavHost(
    practiceListHolder: PracticeListHolder
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination

    val selectedTab = when {
        destination.isTab(AppNav.TAB_SETTINGS) -> MainTab.Settings
        destination.isTab(AppNav.TAB_PRACTICE) -> MainTab.Practice
        else -> MainTab.Lists
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            SpellCoachBottomBar(
                selected = selectedTab,
                onSelect = { tab ->
                    when (tab) {
                        MainTab.Lists -> {
                            navController.navigateToRootTab(AppNav.TAB_LISTS)
                        }

                        MainTab.Practice -> {
                            navController.navigateToRootTab(AppNav.TAB_PRACTICE)
                        }

                        MainTab.Settings -> {
                            navController.navigateToRootTab(AppNav.TAB_SETTINGS)
                        }
                    }
                }
            )
        }
    ) { padding ->
        SpellCoachNavGraph(
            padding = padding,
            navController = navController,
            practiceListHolder = practiceListHolder
        )
    }
}

private fun NavDestination?.isTab(tabRoute: String): Boolean {
    return this?.hierarchy?.any { it.route == tabRoute } == true
}

private fun NavHostController.navigateToRootTab(route: String) {
    navigate(route) {
        popUpTo(AppNav.TAB_LISTS) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun SpellCoachNavGraph(
    padding: PaddingValues,
    navController: NavHostController,
    practiceListHolder: PracticeListHolder
) {
    NavHost(
        navController = navController,
        startDestination = AppNav.TAB_LISTS,
        modifier = Modifier.padding(padding)
    ) {
        navigation(
            route = AppNav.TAB_LISTS,
            startDestination = AppNav.Lists.HOME
        ) {
            composable(AppNav.Lists.HOME) {
                WordListsScreen(
                    onCreateNewList = {
                        navController.navigate(AppNav.listsAddWords(-1L))
                    },
                    onPracticeList = { listId ->
                        practiceListHolder.lastListId = listId

                        navController.navigate(AppNav.TAB_PRACTICE) {
                            popUpTo(AppNav.TAB_LISTS) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onEditList = { listId ->
                        navController.navigate(AppNav.listsAddWords(listId))
                    }
                )
            }

            composable(
                route = AppNav.Lists.ADD_WORDS,
                arguments = listOf(
                    navArgument("listId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) {
                AddWordsScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }
        }

        navigation(
            route = AppNav.TAB_PRACTICE,
            startDestination = AppNav.Practice.ENTRY
        ) {
            composable(AppNav.Practice.ENTRY) {
                PracticeTabEntry(
                    practiceListHolder = practiceListHolder,
                    navController = navController,
                    onOpenListsTab = {
                        navController.navigateToRootTab(AppNav.TAB_LISTS)
                    }
                )
            }

            composable(
                route = AppNav.Practice.SESSION,
                arguments = listOf(
                    navArgument("listId") {
                        type = NavType.LongType
                    }
                )
            ) { entry ->
                val listId = entry.arguments?.getLong("listId") ?: return@composable

                PracticeScreen(
                    onBack = {
                        navController.navigateToRootTab(AppNav.TAB_LISTS)
                    },
                    onFinished = {
                        navController.navigate(AppNav.Practice.RESULTS) {
                            popUpTo(AppNav.Practice.SESSION) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(AppNav.Practice.RESULTS) {
                ResultsScreen(
                    onBack = {
                        navController.navigateToRootTab(AppNav.TAB_LISTS)
                    },
                    onPracticeAgain = { id ->
                        practiceListHolder.lastListId = id

                        navController.navigate(AppNav.practiceSession(id)) {
                            popUpTo(AppNav.Practice.RESULTS) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onGoToLists = {
                        navController.navigateToRootTab(AppNav.TAB_LISTS)
                    }
                )
            }
        }

        navigation(
            route = AppNav.TAB_SETTINGS,
            startDestination = AppNav.Settings.SCREEN
        ) {
            composable(AppNav.Settings.SCREEN) {
                SettingsScreen()
            }
        }
    }
}

@Composable
private fun PracticeTabEntry(
    practiceListHolder: PracticeListHolder,
    navController: NavHostController,
    onOpenListsTab: () -> Unit
) {
    val id = practiceListHolder.lastListId

    LaunchedEffect(id) {
        if (id != null) {
            navController.navigate(AppNav.practiceSession(id)) {
                popUpTo(AppNav.Practice.ENTRY) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    if (id == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Pick a word list, then tap Practice on that list to start.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(onClick = onOpenListsTab) {
                    Text(text = "Go to Lists", color = Color.White)
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}