package com.itclimb.spellcoach.feature.managewords.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itclimb.spellcoach.R
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachOutlinedTextField
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachScreenContainer
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachTopBar
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachTopBarVariant
import com.itclimb.spellcoach.core.designsystem.motion.pressScale
import com.itclimb.spellcoach.core.designsystem.theme.SpellCoachThemeExtras
import com.itclimb.spellcoach.core.designsystem.tokens.AppElevation
import com.itclimb.spellcoach.core.designsystem.tokens.AppIconSize
import com.itclimb.spellcoach.core.designsystem.tokens.AppRadius
import com.itclimb.spellcoach.core.designsystem.tokens.AppSpacing
import com.itclimb.spellcoach.domain.model.Word

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageWordsScreen(
    onBack: () -> Unit,
    onAddWords: () -> Unit,
    viewModel: ManageWordsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val scheme = MaterialTheme.colorScheme
    val extras = SpellCoachThemeExtras.current
    val haptics = LocalHapticFeedback.current

    val required = state.requiredCorrectAnswers.coerceAtLeast(1)
    val displayed = remember(state.words, state.searchQuery, state.filter, required) {
        val q = state.searchQuery.trim().lowercase()
        state.words.asSequence()
            .filter { w -> q.isEmpty() || w.text.lowercase().contains(q) }
            .filter { w ->
                when (state.filter) {
                    ManageWordsFilter.All -> true
                    ManageWordsFilter.Practicing ->
                        w.displayStatus(required) != ManageWordStudyStatus.Mastered
                    ManageWordsFilter.Mastered ->
                        w.displayStatus(required) == ManageWordStudyStatus.Mastered
                }
            }
            .toList()
    }

    val masteredCount = remember(state.words, required) {
        state.words.count { it.displayStatus(required) == ManageWordStudyStatus.Mastered }
    }
    val total = state.words.size

    var wordPendingDelete by remember { mutableStateOf<Word?>(null) }
    var wordPendingReset by remember { mutableStateOf<Word?>(null) }
    var wordBeingEdited by remember { mutableStateOf<Word?>(null) }
    var editDraft by remember { mutableStateOf("") }
    var showBulkDeleteConfirm by remember { mutableStateOf(false) }
    var showBulkResetConfirm by remember { mutableStateOf(false) }

    if (!state.listIdValid) {
        SpellCoachScreenContainer {
            Column(Modifier.fillMaxSize()) {
                SpellCoachTopBar(
                    variant = SpellCoachTopBarVariant.Inner,
                    onBack = onBack,
                    innerTitle = stringResource(R.string.manage_words_title),
                    innerCaption = null,
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(AppSpacing.xxl),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.manage_words_invalid_list),
                        style = MaterialTheme.typography.bodyLarge,
                        color = scheme.onSurfaceVariant
                    )
                }
            }
        }
        return
    }

    val fabInteraction = remember { MutableInteractionSource() }
    val fabContainer = androidx.compose.ui.graphics.lerp(
        scheme.primaryContainer,
        scheme.surface,
        0.18f
    )

    Box(Modifier.fillMaxSize()) {
        SpellCoachScreenContainer {
            Column(Modifier.fillMaxSize()) {
                if (state.multiSelectMode) {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(
                                    R.string.manage_words_selected_count,
                                    state.selection.size
                                ),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { viewModel.exitMultiSelect() }) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = stringResource(R.string.manage_words_exit_selection)
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = { viewModel.markSelectedMastered() },
                                enabled = state.selection.isNotEmpty()
                            ) {
                                Icon(
                                    Icons.Filled.CheckCircle,
                                    contentDescription = stringResource(R.string.manage_words_action_mark_mastered)
                                )
                            }
                            IconButton(
                                onClick = { showBulkResetConfirm = true },
                                enabled = state.selection.isNotEmpty()
                            ) {
                                Icon(
                                    Icons.Filled.RestartAlt,
                                    contentDescription = stringResource(R.string.manage_words_action_reset_selected)
                                )
                            }
                            IconButton(
                                onClick = { showBulkDeleteConfirm = true },
                                enabled = state.selection.isNotEmpty()
                            ) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = stringResource(R.string.manage_words_action_delete_selected)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = scheme.surfaceContainerHigh.copy(alpha = 0.92f),
                            titleContentColor = scheme.onSurface,
                            navigationIconContentColor = scheme.onSurfaceVariant,
                            actionIconContentColor = scheme.onSurfaceVariant
                        )
                    )
                } else {
                    SpellCoachTopBar(
                        variant = SpellCoachTopBarVariant.Inner,
                        onBack = onBack,
                        innerTitle = stringResource(R.string.manage_words_title),
                        innerCaption = stringResource(
                            R.string.manage_words_subtitle_format,
                            total,
                            masteredCount
                        ),
                    )
                }

                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppSpacing.screenHorizontal)
                ) {
                    if (!state.multiSelectMode) {
                        SpellCoachOutlinedTextField(
                            value = state.searchQuery,
                            onValueChange = viewModel::setSearchQuery,
                            placeholder = stringResource(R.string.manage_words_search_placeholder),
                            modifier = Modifier.padding(top = AppSpacing.sm)
                        )
                        Spacer(Modifier.height(AppSpacing.md))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                        ) {
                            ManageWordsFilterChip(
                                label = stringResource(R.string.manage_words_filter_all),
                                selected = state.filter == ManageWordsFilter.All,
                                onClick = { viewModel.setFilter(ManageWordsFilter.All) }
                            )
                            ManageWordsFilterChip(
                                label = stringResource(R.string.manage_words_filter_practicing),
                                selected = state.filter == ManageWordsFilter.Practicing,
                                onClick = { viewModel.setFilter(ManageWordsFilter.Practicing) }
                            )
                            ManageWordsFilterChip(
                                label = stringResource(R.string.manage_words_filter_mastered),
                                selected = state.filter == ManageWordsFilter.Mastered,
                                onClick = { viewModel.setFilter(ManageWordsFilter.Mastered) }
                            )
                        }
                        Spacer(Modifier.height(AppSpacing.md + AppSpacing.xs))
                    } else {
                        Spacer(Modifier.height(AppSpacing.sm))
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(
                        start = AppSpacing.screenHorizontal,
                        end = AppSpacing.screenHorizontal,
                        bottom = AppSpacing.fabClearance
                    ),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.md + AppSpacing.xs)
                ) {
                    if (displayed.isEmpty()) {
                        item {
                            Text(
                                text = if (state.words.isEmpty()) {
                                    stringResource(R.string.manage_words_empty_list)
                                } else {
                                    stringResource(R.string.manage_words_empty_filter)
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = scheme.onSurfaceVariant.copy(alpha = 0.88f),
                                modifier = Modifier.padding(top = AppSpacing.xxl)
                            )
                        }
                    } else {
                        items(displayed, key = { it.id }) { word ->
                            val status = word.displayStatus(required)
                            val mastered = status == ManageWordStudyStatus.Mastered
                            val selected = word.id in state.selection
                            ManageWordRowCard(
                                word = word,
                                status = status,
                                mastered = mastered,
                                multiSelectMode = state.multiSelectMode,
                                selected = selected,
                                onRowClick = {
                                    if (state.multiSelectMode) {
                                        viewModel.toggleSelection(word.id)
                                    }
                                },
                                onRowLongPress = {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    if (state.multiSelectMode) {
                                        viewModel.toggleSelection(word.id)
                                    } else {
                                        viewModel.enterMultiSelect(word.id)
                                    }
                                },
                                onPlay = { viewModel.speakWord(word.text) },
                                onToggleMastered = { viewModel.toggleMastered(word) },
                                onEdit = {
                                    wordBeingEdited = word
                                    editDraft = word.text
                                },
                                onReset = { wordPendingReset = word },
                                onDelete = { wordPendingDelete = word },
                                successColor = extras.success,
                                progressMastered = extras.progressMastered
                            )
                        }
                    }
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = onAddWords,
            interactionSource = fabInteraction,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .pressScale(fabInteraction, pressedScale = 0.94f)
                .padding(end = AppSpacing.lg, bottom = AppSpacing.fabBottomInset),
            containerColor = fabContainer,
            contentColor = scheme.primary,
            shape = RoundedCornerShape(AppRadius.xl),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = AppElevation.level4,
                pressedElevation = AppElevation.level2,
            ),
            icon = {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(AppIconSize.xl)
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.manage_words_add_words),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        )
    }

    wordPendingDelete?.let { w ->
        AlertDialog(
            onDismissRequest = { wordPendingDelete = null },
            title = { Text(stringResource(R.string.manage_words_dialog_delete_title)) },
            text = { Text(stringResource(R.string.manage_words_dialog_delete_message, w.text)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteWord(w.id)
                        wordPendingDelete = null
                    }
                ) {
                    Text(stringResource(R.string.action_delete))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { wordPendingDelete = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    wordPendingReset?.let { w ->
        AlertDialog(
            onDismissRequest = { wordPendingReset = null },
            title = { Text(stringResource(R.string.manage_words_dialog_reset_title)) },
            text = { Text(stringResource(R.string.manage_words_dialog_reset_message, w.text)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetWordProgress(w.id)
                        wordPendingReset = null
                    }
                ) {
                    Text(stringResource(R.string.action_reset))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { wordPendingReset = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    wordBeingEdited?.let { w ->
        AlertDialog(
            onDismissRequest = { wordBeingEdited = null },
            title = { Text(stringResource(R.string.manage_words_dialog_edit_title)) },
            text = {
                OutlinedTextField(
                    value = editDraft,
                    onValueChange = { editDraft = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(AppRadius.lg)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.renameWord(w.id, editDraft)
                        wordBeingEdited = null
                    },
                    enabled = editDraft.trim().isNotEmpty()
                ) {
                    Text(stringResource(R.string.action_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { wordBeingEdited = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    if (showBulkDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showBulkDeleteConfirm = false },
            title = { Text(stringResource(R.string.manage_words_dialog_bulk_delete_title)) },
            text = {
                Text(
                    stringResource(
                        R.string.manage_words_dialog_bulk_delete_message,
                        state.selection.size
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSelected()
                        showBulkDeleteConfirm = false
                    }
                ) {
                    Text(stringResource(R.string.action_delete))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showBulkDeleteConfirm = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    if (showBulkResetConfirm) {
        AlertDialog(
            onDismissRequest = { showBulkResetConfirm = false },
            title = { Text(stringResource(R.string.manage_words_dialog_bulk_reset_title)) },
            text = {
                Text(
                    stringResource(
                        R.string.manage_words_dialog_bulk_reset_message,
                        state.selection.size
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetSelectedProgress()
                        showBulkResetConfirm = false
                    }
                ) {
                    Text(stringResource(R.string.action_reset))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showBulkResetConfirm = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun ManageWordsFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, style = MaterialTheme.typography.labelLarge) },
        shape = RoundedCornerShape(AppRadius.pill),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = scheme.outlineVariant.copy(alpha = 0.35f)
        ),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = scheme.surface.copy(alpha = 0.55f),
            selectedContainerColor = scheme.secondaryContainer.copy(alpha = 0.85f),
            labelColor = scheme.onSurfaceVariant,
            selectedLabelColor = scheme.onSecondaryContainer
        )
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ManageWordRowCard(
    word: Word,
    status: ManageWordStudyStatus,
    mastered: Boolean,
    multiSelectMode: Boolean,
    selected: Boolean,
    onRowClick: () -> Unit,
    onRowLongPress: () -> Unit,
    onPlay: () -> Unit,
    onToggleMastered: () -> Unit,
    onEdit: () -> Unit,
    onReset: () -> Unit,
    onDelete: () -> Unit,
    successColor: androidx.compose.ui.graphics.Color,
    progressMastered: androidx.compose.ui.graphics.Color,
) {
    val scheme = MaterialTheme.colorScheme
    val isLight = scheme.background.luminance() > 0.5f
    val baseSurface = scheme.surfaceContainerLow
    val containerColor = when {
        selected -> lerp(baseSurface, scheme.primaryContainer, if (isLight) 0.35f else 0.28f)
        mastered -> lerp(baseSurface, successColor, if (isLight) 0.12f else 0.16f)
        else -> lerp(baseSurface, scheme.primaryContainer, if (isLight) 0.04f else 0.07f)
    }
    val shape = RoundedCornerShape(AppRadius.glassCard)
    var menuOpen by remember { mutableStateOf(false) }

    val borderColor = when {
        selected -> scheme.primary.copy(alpha = 0.35f)
        mastered -> progressMastered.copy(alpha = 0.22f)
        else -> scheme.outlineVariant.copy(alpha = if (isLight) 0.08f else 0.12f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onRowClick,
                onLongClick = onRowLongPress,
            ),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = AppElevation.level2,
            pressedElevation = AppElevation.level1,
        ),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md + AppSpacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (multiSelectMode) {
                Icon(
                    imageVector = if (selected) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (selected) scheme.primary else scheme.onSurfaceVariant.copy(alpha = 0.55f),
                    modifier = Modifier.size(AppIconSize.xl)
                )
                Spacer(Modifier.width(AppSpacing.md))
            }
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = word.text,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 22.sp,
                            lineHeight = 28.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.2).sp
                        ),
                        color = if (mastered) {
                            scheme.onSurface.copy(alpha = 0.82f)
                        } else {
                            scheme.onSurface
                        }
                    )
                    if (mastered) {
                        Spacer(Modifier.width(AppSpacing.sm))
                        Box(
                            modifier = Modifier
                                .size(AppIconSize.lg + AppSpacing.xs)
                                .clip(CircleShape)
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                        }
                    }
                }
                Spacer(Modifier.height(AppSpacing.xs + AppSpacing.xxs))
                val (label, labelColor) = when (status) {
                    ManageWordStudyStatus.New ->
                        stringResource(R.string.manage_words_status_new) to scheme.onSurfaceVariant.copy(alpha = 0.85f)
                    ManageWordStudyStatus.Practicing ->
                        stringResource(R.string.manage_words_status_practicing) to scheme.tertiary
                    ManageWordStudyStatus.Mastered ->
                        stringResource(R.string.manage_words_status_mastered) to progressMastered
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = labelColor
                )
            }
            IconButton(onClick = onPlay) {
                Icon(
                    Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = stringResource(R.string.content_desc_listen),
                    tint = scheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onToggleMastered) {
                Icon(
                    imageVector = if (mastered) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                    contentDescription = stringResource(R.string.manage_words_content_desc_toggle_mastered),
                    tint = if (mastered) progressMastered else scheme.onSurfaceVariant
                )
            }
            Box {
                IconButton(onClick = { menuOpen = true }) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = stringResource(R.string.manage_words_content_desc_word_menu),
                        tint = scheme.onSurfaceVariant
                    )
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.manage_words_menu_edit)) },
                        leadingIcon = {
                            Icon(Icons.Filled.Edit, contentDescription = null)
                        },
                        onClick = {
                            menuOpen = false
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.manage_words_menu_reset)) },
                        leadingIcon = {
                            Icon(Icons.Filled.RestartAlt, contentDescription = null)
                        },
                        onClick = {
                            menuOpen = false
                            onReset()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.manage_words_menu_delete)) },
                        leadingIcon = {
                            Icon(Icons.Filled.Delete, contentDescription = null)
                        },
                        onClick = {
                            menuOpen = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}
