package com.itclimb.spellcoach.core.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
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
import com.itclimb.spellcoach.core.designsystem.tokens.AppDimensions
import com.itclimb.spellcoach.core.designsystem.tokens.AppSpacing
import com.itclimb.spellcoach.feature.addwords.presentation.AddWordsScreen
import com.itclimb.spellcoach.feature.managewords.presentation.ManageWordsScreen
import com.itclimb.spellcoach.feature.practice.PracticeListHolder
import com.itclimb.spellcoach.feature.practice.presentation.PracticeScreen
import com.itclimb.spellcoach.feature.settings.presentation.SettingsScreen
import com.itclimb.spellcoach.feature.wordlists.presentation.WordListsScreen

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

/**
 * Opens practice for [listId] without restoring a previously saved practice back stack.
 * Prefer [popUpToEntryId] (concrete back stack entry id) over route templates when replacing
 * a stale session destination.
 */
private fun NavHostController.navigateToPracticeSession(
    listId: Long,
    popUpToEntryId: String? = null,
    popUpToListsTab: Boolean = false,
) {
    navigate(AppNav.practiceSession(listId)) {
        when {
            popUpToEntryId != null -> {
                popUpTo(popUpToEntryId) {
                    inclusive = true
                    saveState = false
                }
            }
            popUpToListsTab -> {
                popUpTo(AppNav.TAB_LISTS) {
                    saveState = true
                    inclusive = false
                }
            }
        }
        launchSingleTop = true
        restoreState = false
    }
}

private fun NavHostController.requestPracticeSession(
    listId: Long,
    practiceListHolder: PracticeListHolder,
) {
    practiceListHolder.pendingPracticeListId = listId
    navigateToPracticeSession(listId, popUpToListsTab = true)
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
                        navController.requestPracticeSession(listId, practiceListHolder)
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
                val sessionListId = entry.arguments?.getLong("listId") ?: return@composable

                PracticeSessionRouteGuard(
                    entry = entry,
                    sessionListId = sessionListId,
                    practiceListHolder = practiceListHolder,
                    navController = navController,
                ) {
                    PracticeScreen(
                        onBack = {
                            navController.navigateToRootTab(AppNav.TAB_LISTS)
                        },
                        viewModel = hiltViewModel(entry),
                    )
                }
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

/**
 * Blocks interaction until the route's [sessionListId] matches an explicit pending request.
 * Replaces stale sessions using [NavBackStackEntry.id], not route templates.
 */
@Composable
private fun PracticeSessionRouteGuard(
    entry: NavBackStackEntry,
    sessionListId: Long,
    practiceListHolder: PracticeListHolder,
    navController: NavHostController,
    content: @Composable () -> Unit,
) {
    val pending = practiceListHolder.pendingPracticeListId
    val sessionReady = practiceListHolder.isExplicitSessionReady(sessionListId)

    LaunchedEffect(entry.id, sessionListId, pending) {
        if (sessionReady) {
            practiceListHolder.clearPendingIfMatches(sessionListId)
            return@LaunchedEffect
        }

        val pendingListId = practiceListHolder.pendingPracticeListId ?: return@LaunchedEffect
        navController.navigateToPracticeSession(
            listId = pendingListId,
            popUpToEntryId = entry.id,
        )
    }

    if (sessionReady) {
        content()
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun PracticeTabEntry(
    practiceListHolder: PracticeListHolder,
    navController: NavHostController,
    onOpenListsTab: () -> Unit
) {
    val id = practiceListHolder.lastListId

    LaunchedEffect(id) {
        if (id != null) {
            val entry = navController.currentBackStackEntry ?: return@LaunchedEffect
            val onMatchingSession = entry.destination.route == AppNav.Practice.SESSION &&
                entry.arguments?.getLong("listId") == id
            if (onMatchingSession) return@LaunchedEffect

            navController.navigateToPracticeSession(
                listId = id,
                popUpToEntryId = entry.id,
            )
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
                    BoxWithConstraints(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        val mascot =
                            (maxWidth * AppDimensions.mascotPromoWidthFraction).coerceIn(
                                AppDimensions.mascotPromoMin,
                                AppDimensions.mascotPromoMax
                            )

                        Image(
                            painter = painterResource(R.drawable.fox_advice_pose),
                            contentDescription = null,
                            modifier = Modifier
                                .size(mascot)
                                .alpha(0.95f)
                        )
                    }

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
