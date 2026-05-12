package com.example.spellcoach.feature.wordlists.presentation

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spellcoach.R
import com.example.spellcoach.core.designsystem.components.DesignProgressBar
import com.example.spellcoach.core.designsystem.components.LearningCard
import com.example.spellcoach.core.designsystem.components.SpellCoachPrimaryButton
import com.example.spellcoach.core.designsystem.components.SpellCoachScreenContainer
import com.example.spellcoach.core.designsystem.components.SpellCoachTopBar
import com.example.spellcoach.core.designsystem.theme.SpellCoachThemeExtras
import com.example.spellcoach.core.designsystem.tokens.AppElevation
import com.example.spellcoach.core.designsystem.tokens.AppIconSize
import com.example.spellcoach.core.designsystem.tokens.AppRadius
import com.example.spellcoach.core.designsystem.tokens.AppSpacing
import com.example.spellcoach.domain.model.WordList

@Composable
fun WordListsScreen(
    onCreateNewList: () -> Unit,
    onPracticeList: (Long) -> Unit,
    onEditList: (Long) -> Unit,
    viewModel: WordListsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val scheme = MaterialTheme.colorScheme

    Box(modifier = Modifier.fillMaxSize()) {
        SpellCoachScreenContainer {
            Column(Modifier.fillMaxSize()) {
                SpellCoachTopBar(
                    showBack = false,
                    onBack = {},
                    brandTitle = stringResource(R.string.app_name),
                    screenTitle = null,
                    subtitleBelowBrand = stringResource(R.string.lists_subtitle)
                )
                when {
                    state.loading -> Unit
                    state.lists.isEmpty() -> {
                        EmptyWordLists(
                            onCreateNewList = onCreateNewList,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(AppSpacing.screenHorizontal)
                        )
                    }
                    else -> {
                        LazyColumn(
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                start = AppSpacing.screenHorizontal,
                                end = AppSpacing.screenHorizontal,
                                top = AppSpacing.sm + AppSpacing.xs,
                                bottom = AppSpacing.fabClearance
                            ),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
                    ) {
                        items(state.lists) { list ->
                            WordListCard(
                                list = list,
                                onClick = {
                                    viewModel.rememberPracticeList(list.id)
                                    onPracticeList(list.id)
                                },
                                onEdit = { onEditList(list.id) },
                                onResetProgress = { viewModel.resetListProgress(list.id) },
                                onDelete = { viewModel.deleteList(list.id) }
                            )
                        }
                    }
                }
            }
        }
        }
        FloatingActionButton(
            onClick = onCreateNewList,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = AppSpacing.xl, bottom = AppSpacing.xxxl - AppSpacing.sm),
            containerColor = scheme.primaryContainer,
            contentColor = scheme.primary,
            shape = RoundedCornerShape(AppRadius.lg),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = AppElevation.level2,
                pressedElevation = AppElevation.level1
            )
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = stringResource(R.string.content_desc_add_word_list)
            )
        }
    }
}

@Composable
private fun EmptyWordLists(
    onCreateNewList: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition(label = "empty_mascot")
    val offsetY by infinite.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.fox_neutral),
            contentDescription = stringResource(R.string.content_desc_empty_mascot),
            modifier = Modifier
                .size(AppIconSize.mascotMedium)
                .graphicsLayer { translationY = offsetY }
        )
        Spacer(Modifier.height(AppSpacing.lg))
        Text(
            text = stringResource(R.string.lists_empty_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(AppSpacing.sm))
        Text(
            text = stringResource(R.string.lists_empty_message),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(AppSpacing.xxl))
        SpellCoachPrimaryButton(
            text = stringResource(R.string.lists_empty_cta),
            onClick = onCreateNewList
        )
    }
}

@Composable
private fun WordListCard(
    list: WordList,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onResetProgress: () -> Unit,
    onDelete: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val scheme = MaterialTheme.colorScheme
    val extras = SpellCoachThemeExtras.current

    LearningCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = list.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = scheme.onSurface
                )
                Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))
                Text(
                    text = stringResource(
                        R.string.lists_progress_format,
                        list.learnedWords,
                        list.totalWords
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (list.isMastered) {
                    Box(
                        modifier = Modifier
                            .size(AppIconSize.xxl)
                            .clip(CircleShape)
                            .background(extras.progressFill.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = stringResource(R.string.content_desc_list_mastered),
                            tint = extras.progressMastered,
                            modifier = Modifier.size(AppIconSize.sm)
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = stringResource(R.string.content_desc_open_list),
                        tint = scheme.onSurfaceVariant,
                        modifier = Modifier.size(AppIconSize.xl)
                    )
                }
                Spacer(Modifier.width(AppSpacing.sm + AppSpacing.xs))
                IconButton(onClick = { menuOpen = true }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = stringResource(R.string.content_desc_list_menu),
                        tint = scheme.onSurfaceVariant
                    )
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.menu_edit)) },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = stringResource(R.string.menu_edit)
                            )
                        },
                        onClick = {
                            menuOpen = false
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.menu_reset_progress)) },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.RestartAlt,
                                contentDescription = stringResource(R.string.menu_reset_progress)
                            )
                        },
                        onClick = {
                            menuOpen = false
                            showResetConfirm = true
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.menu_delete)) },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = stringResource(R.string.menu_delete)
                            )
                        },
                        onClick = {
                            menuOpen = false
                            showDeleteConfirm = true
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(AppSpacing.md + AppSpacing.xs))
        DesignProgressBar(
            progress = list.progress,
            fullMastered = list.isMastered
        )
        Spacer(Modifier.height(AppSpacing.md + AppSpacing.xs))
        if (list.isMastered) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(AppSpacing.lg + AppSpacing.sm)
                        .clip(CircleShape)
                        .background(extras.progressFill.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = extras.progressMastered,
                        modifier = Modifier.size(AppIconSize.xs)
                    )
                }
                Spacer(Modifier.width(AppSpacing.sm))
                Text(
                    text = stringResource(R.string.lists_mastered_badge),
                    color = extras.progressMastered,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        } else if (list.chips.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm + AppSpacing.xs)) {
                list.chips.take(2).forEachIndexed { idx, chip ->
                    val bg = if (idx == 0) extras.chipBlueBg else extras.chipPurpleBg
                    val fg = if (idx == 0) extras.chipBlueFg else extras.chipPurpleFg
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AppRadius.pill))
                            .background(bg)
                            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm)
                    ) {
                        Text(
                            text = chip,
                            color = fg,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text(text = stringResource(R.string.dialog_reset_list_title)) },
            text = { Text(text = stringResource(R.string.dialog_reset_list_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirm = false
                        onResetProgress()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.action_reset),
                        color = scheme.onPrimary
                    )
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetConfirm = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(text = stringResource(R.string.dialog_delete_list_title)) },
            text = { Text(text = stringResource(R.string.dialog_delete_list_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.action_delete),
                        color = scheme.onPrimary
                    )
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}
