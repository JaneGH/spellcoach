package com.itclimb.spellcoach.core.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.itclimb.spellcoach.R
import com.itclimb.spellcoach.core.designsystem.components.MainTab
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachBottomBar
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachPrimaryButton
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachScreenContainer
import com.itclimb.spellcoach.feature.addwords.presentation.AddWordsScreen
import com.itclimb.spellcoach.feature.managewords.presentation.ManageWordsScreen
import com.itclimb.spellcoach.feature.practice.presentation.PracticeScreen
import com.itclimb.spellcoach.feature.results.presentation.ResultsScreen
import com.itclimb.spellcoach.feature.settings.presentation.SettingsScreen
import com.itclimb.spellcoach.feature.wordlists.presentation.WordListsScreen
import com.itclimb.spellcoach.core.designsystem.tokens.AppSpacing
import com.itclimb.spellcoach.feature.practice.PracticeListHolder

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
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            SpellCoachBottomBar(
                selected = selectedTab,
                onSelect = { tab ->
                    when (tab) {
                        MainTab.Lists -> navController.navigateToRootTab(AppNav.TAB_LISTS)
                        MainTab.Practice -> navController.navigateToRootTab(AppNav.TAB_PRACTICE)
                        MainTab.Settings -> navController.navigateToRootTab(AppNav.TAB_SETTINGS)
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
                    },
                    onManageWords = { listId ->
                        navController.navigate(AppNav.listsManageWords(listId))
                    }
                )
            }

            composable(
                route = AppNav.Lists.MANAGE_WORDS,
                arguments = listOf(
                    navArgument("listId") {
                        type = NavType.LongType
                    }
                )
            ) { entry ->
                val listId = entry.arguments?.getLong("listId") ?: return@composable
                ManageWordsScreen(
                    onBack = { navController.popBackStack() },
                    onAddWords = {
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
        SpellCoachScreenContainer {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(AppSpacing.xxl)
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .padding(horizontal = AppSpacing.xxl),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.fox_advice_pose),
                        contentDescription = null,
                        modifier = Modifier
                            .size(96.dp)
                            .alpha(0.95f)
                    )
                    Spacer(Modifier.height(AppSpacing.sm))
                    Text(
                        text = stringResource(R.string.practice_pick_list_message),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(AppSpacing.lg))
                    SpellCoachPrimaryButton(
                        text = stringResource(R.string.practice_go_to_lists),
                        onClick = onOpenListsTab
                    )
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
