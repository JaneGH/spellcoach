package com.example.spellcoach.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.spellcoach.presentation.addwords.AddWordsScreen
import com.example.spellcoach.presentation.components.MainTab
import com.example.spellcoach.presentation.components.SpellCoachBottomBar
import com.example.spellcoach.presentation.practice.PracticeScreen
import com.example.spellcoach.presentation.results.ResultsScreen
import com.example.spellcoach.presentation.settings.SettingsScreen
import com.example.spellcoach.presentation.wordlists.WordListsScreen

@Composable
fun SpellCoachNavHost(practiceListHolder: PracticeListHolder) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination

    val selectedTab = when {
        destination?.hierarchy?.any { it.route == Route.Settings.path } == true -> MainTab.Settings
        destination?.route?.startsWith("practice") == true -> MainTab.Practice
        else -> MainTab.Lists
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            SpellCoachBottomBar(
                selected = selectedTab,
                onSelect = { tab ->
                    when (tab) {
                        MainTab.Lists -> navController.navigate(Route.WordLists.path) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        MainTab.Practice -> {
                            val id = practiceListHolder.lastListId
                            if (id != null) {
                                navController.navigate(Route.Practice.create(id)) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                }
                            } else {
                                navController.navigate(Route.WordLists.path) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        MainTab.Settings -> navController.navigate(Route.Settings.path) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { padding ->
        SpellCoachNavGraph(padding, navController = navController)
    }
}

@Composable
private fun SpellCoachNavGraph(
    padding: PaddingValues,
    navController: androidx.navigation.NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Route.WordLists.path,
        modifier = Modifier.padding(padding)
    ) {
        composable(Route.WordLists.path) {
            WordListsScreen(
                onCreateNewList = { navController.navigate(Route.AddWords.createNew()) },
                onPracticeList = { listId ->
                    navController.navigate(Route.Practice.create(listId))
                },
                onEditList = { listId ->
                    navController.navigate(Route.AddWords.edit(listId))
                }
            )
        }
        composable(
            route = Route.AddWords.path,
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
        composable(
            route = Route.Practice.path,
            arguments = listOf(navArgument("listId") { type = NavType.LongType })
        ) {
            PracticeScreen(
                onBack = { navController.popBackStack() },
                onFinished = {
                    navController.navigate(Route.Results.path) {
                        popUpTo(Route.WordLists.path) { inclusive = false }
                    }
                }
            )
        }
        composable(Route.Results.path) {
            ResultsScreen(
                onBack = { navController.popBackStack() },
                onPracticeAgain = { listId ->
                    navController.navigate(Route.Practice.create(listId)) {
                        popUpTo(Route.WordLists.path) { inclusive = false }
                    }
                },
                onGoToLists = {
                    navController.popBackStack(Route.WordLists.path, inclusive = false)
                }
            )
        }
        composable(Route.Settings.path) {
            SettingsScreen()
        }
    }
}
