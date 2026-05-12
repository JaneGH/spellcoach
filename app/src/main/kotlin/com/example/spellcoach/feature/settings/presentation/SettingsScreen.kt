package com.example.spellcoach.feature.settings.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spellcoach.R
import com.example.spellcoach.core.designsystem.components.LearningCard
import com.example.spellcoach.core.designsystem.components.SpellCoachPrimaryButton
import com.example.spellcoach.core.designsystem.components.SpellCoachScreenContainer
import com.example.spellcoach.core.designsystem.components.SpellCoachTopBar
import com.example.spellcoach.core.designsystem.components.spellCoachScreenHorizontalPadding
import com.example.spellcoach.core.designsystem.theme.SpellCoachThemeExtras
import com.example.spellcoach.core.designsystem.tokens.AppDimensions
import com.example.spellcoach.core.designsystem.tokens.AppRadius
import com.example.spellcoach.core.designsystem.tokens.AppSpacing
import com.example.spellcoach.data.tts.TtsAvailability
import com.example.spellcoach.domain.model.MistakeBehavior
import com.example.spellcoach.domain.model.ThemePreference

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()
    val ttsAvailability by viewModel.ttsAvailability.collectAsState()
    var showResetAllConfirm by remember { mutableStateOf(false) }
    val scheme = MaterialTheme.colorScheme
    val extras = SpellCoachThemeExtras.current

    SpellCoachScreenContainer {
        SpellCoachTopBar(
            showBack = false,
            onBack = {},
            brandTitle = stringResource(R.string.app_name),
            brandAccent = null,
            screenTitle = null,
            heroTitle = stringResource(R.string.settings_hero_title),
            subtitleBelowBrand = stringResource(R.string.settings_subtitle)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .spellCoachScreenHorizontalPadding()
                .padding(bottom = AppSpacing.sheetBottom)
        ) {
            Spacer(Modifier.height(AppSpacing.md))

            if (ttsAvailability != TtsAvailability.Ready && ttsAvailability != TtsAvailability.Checking) {
                Text(
                    text = stringResource(R.string.settings_tts_unavailable),
                    color = scheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = AppSpacing.sm + AppSpacing.xs)
                )
            }

            LearningCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.settings_theme_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = scheme.onSurface
                )
                Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))
                Text(
                    text = stringResource(R.string.settings_theme_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant
                )
                Spacer(Modifier.height(AppSpacing.sm + AppSpacing.md))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                ) {
                    val chipColors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = scheme.primaryContainer,
                        selectedLabelColor = scheme.onPrimaryContainer,
                        selectedLeadingIconColor = scheme.onPrimaryContainer,
                        containerColor = scheme.surfaceVariant.copy(alpha = 0.35f),
                        labelColor = scheme.onSurfaceVariant
                    )
                    FilterChip(
                        selected = settings.themePreference == ThemePreference.SYSTEM,
                        onClick = { viewModel.setThemePreference(ThemePreference.SYSTEM) },
                        label = { Text(stringResource(R.string.settings_theme_system)) },
                        modifier = Modifier.weight(1f),
                        colors = chipColors
                    )
                    FilterChip(
                        selected = settings.themePreference == ThemePreference.LIGHT,
                        onClick = { viewModel.setThemePreference(ThemePreference.LIGHT) },
                        label = { Text(stringResource(R.string.settings_theme_light)) },
                        modifier = Modifier.weight(1f),
                        colors = chipColors
                    )
                    FilterChip(
                        selected = settings.themePreference == ThemePreference.DARK,
                        onClick = { viewModel.setThemePreference(ThemePreference.DARK) },
                        label = { Text(stringResource(R.string.settings_theme_dark)) },
                        modifier = Modifier.weight(1f),
                        colors = chipColors
                    )
                }
            }

            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.md))

            LearningCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(AppRadius.md))
                            .background(scheme.primaryContainer.copy(alpha = 0.45f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = stringResource(R.string.content_desc_required_correct_icon),
                            tint = scheme.primary
                        )
                    }
                    Spacer(Modifier.width(AppSpacing.sm + AppSpacing.md))
                    Column {
                        Text(
                            text = stringResource(R.string.settings_required_correct_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = scheme.onSurface
                        )
                        Spacer(Modifier.height(AppSpacing.xs))
                        Text(
                            text = stringResource(R.string.settings_required_correct_body),
                            style = MaterialTheme.typography.bodyMedium,
                            color = scheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(AppSpacing.sm + AppSpacing.md))
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm + AppSpacing.xs)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm + AppSpacing.xs)
                    ) {
                        (1..5).forEach { n ->
                            NumberPickCell(
                                n = n,
                                selected = n == settings.requiredCorrectAnswers,
                                onClick = { viewModel.setRequiredCorrect(n) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm + AppSpacing.xs)
                    ) {
                        (6..10).forEach { n ->
                            NumberPickCell(
                                n = n,
                                selected = n == settings.requiredCorrectAnswers,
                                onClick = { viewModel.setRequiredCorrect(n) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.md))

            LearningCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(AppRadius.md))
                            .background(scheme.tertiaryContainer.copy(alpha = 0.55f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = stringResource(R.string.content_desc_mistake_behavior_icon),
                            tint = scheme.tertiary
                        )
                    }
                    Spacer(Modifier.width(AppSpacing.sm + AppSpacing.md))
                    Column {
                        Text(
                            text = stringResource(R.string.settings_mistake_behavior_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = scheme.onSurface
                        )
                        Spacer(Modifier.height(AppSpacing.xs))
                        Text(
                            text = stringResource(R.string.settings_mistake_behavior_body),
                            style = MaterialTheme.typography.bodyMedium,
                            color = scheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(AppSpacing.sm + AppSpacing.md))
                MistakeOptionRow(
                    title = stringResource(R.string.settings_mistake_decrease),
                    icon = Icons.AutoMirrored.Filled.TrendingDown,
                    selected = settings.mistakeBehavior == MistakeBehavior.DECREASE_PROGRESS,
                    onClick = { viewModel.setMistakeBehavior(MistakeBehavior.DECREASE_PROGRESS) }
                )
                Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))
                MistakeOptionRow(
                    title = stringResource(R.string.settings_mistake_reset),
                    icon = Icons.Filled.Refresh,
                    selected = settings.mistakeBehavior == MistakeBehavior.RESET_PROGRESS,
                    onClick = { viewModel.setMistakeBehavior(MistakeBehavior.RESET_PROGRESS) }
                )
            }

            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.md))

            LearningCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.settings_progress_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = scheme.onSurface
                )
                Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))
                Text(
                    text = stringResource(R.string.settings_progress_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant
                )
                Spacer(Modifier.height(AppSpacing.md))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(AppRadius.md))
                        .background(scheme.errorContainer.copy(alpha = 0.42f))
                        .border(
                            1.dp,
                            scheme.outlineVariant.copy(alpha = 0.55f),
                            RoundedCornerShape(AppRadius.md)
                        )
                        .clickable { showResetAllConfirm = true }
                        .padding(vertical = AppSpacing.sm + AppSpacing.md),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.settings_reset_all_progress),
                        color = scheme.error,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.md))

            LearningCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(AppRadius.md))
                                .background(scheme.secondaryContainer.copy(alpha = 0.45f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = stringResource(R.string.content_desc_audio_icon),
                                tint = scheme.secondary
                            )
                        }
                        Spacer(Modifier.width(AppSpacing.sm + AppSpacing.md))
                        Column {
                            Text(
                                text = stringResource(R.string.settings_audio_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = scheme.onSurface
                            )
                            Text(
                                text = stringResource(R.string.settings_audio_body),
                                style = MaterialTheme.typography.bodyMedium,
                                color = scheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = settings.audioEnabled,
                        onCheckedChange = viewModel::setAudioEnabled,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = scheme.onPrimary,
                            checkedTrackColor = scheme.primary
                        )
                    )
                }
            }

            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.md))

            LearningCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.settings_letter_hints_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = scheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.settings_letter_hints_body),
                            style = MaterialTheme.typography.bodyMedium,
                            color = scheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = settings.letterHintsEnabled,
                        onCheckedChange = viewModel::setLetterHintsEnabled,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = scheme.onPrimary,
                            checkedTrackColor = scheme.primary
                        )
                    )
                }
            }

            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.md))

            LearningCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.settings_speech_rate_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = scheme.onSurface
                )
                Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))
                Text(
                    text = stringResource(R.string.settings_speech_rate_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant
                )
                Slider(
                    value = settings.speechRate,
                    onValueChange = viewModel::setSpeechRate,
                    valueRange = 0.5f..2f,
                    colors = SliderDefaults.colors(
                        thumbColor = scheme.primary,
                        activeTrackColor = scheme.primary
                    )
                )
                Text(
                    text = stringResource(R.string.speech_rate_format, settings.speechRate),
                    fontWeight = FontWeight.Bold,
                    color = scheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.md))

            LearningCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.settings_reward_sounds_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = scheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.settings_reward_sounds_body),
                            style = MaterialTheme.typography.bodyMedium,
                            color = scheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = settings.rewardSoundsEnabled,
                        onCheckedChange = viewModel::setRewardSounds,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = scheme.onPrimary,
                            checkedTrackColor = scheme.primary
                        )
                    )
                }
                Spacer(Modifier.height(AppSpacing.sm + AppSpacing.md))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.settings_animations_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = scheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.settings_animations_body),
                            style = MaterialTheme.typography.bodyMedium,
                            color = scheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = settings.animationsEnabled,
                        onCheckedChange = viewModel::setAnimations,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = scheme.onPrimary,
                            checkedTrackColor = scheme.primary
                        )
                    )
                }
            }

            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.md))

            SpellCoachPrimaryButton(
                text = stringResource(R.string.settings_open_tts),
                onClick = viewModel::openTtsSettings
            )

            Spacer(Modifier.height(AppSpacing.lg))

            val mascotBg = extras.mascotPanel
            val mascotFg = scheme.contentColorFor(mascotBg)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimensions.settingsBannerHeight)
                    .clip(RoundedCornerShape(AppRadius.xxxl))
                    .background(mascotBg),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = stringResource(R.string.settings_mascot_quote),
                    color = mascotFg,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(AppSpacing.lg)
                )

                Image(
                    painter = painterResource(R.drawable.fox_happy2),
                    contentDescription = stringResource(R.string.content_desc_settings_mascot),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = AppSpacing.sm, bottom = AppSpacing.sm + AppSpacing.xs)
                        .size(84.dp)
                )
            }
        }
    }

    if (showResetAllConfirm) {
        AlertDialog(
            onDismissRequest = { showResetAllConfirm = false },
            title = { Text(text = stringResource(R.string.dialog_reset_all_title)) },
            text = { Text(text = stringResource(R.string.dialog_reset_all_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        showResetAllConfirm = false
                        viewModel.resetAllProgress()
                    }
                ) { Text(stringResource(R.string.action_reset)) }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetAllConfirm = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun NumberPickCell(
    n: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val borderColor = if (selected) {
        scheme.primary.copy(alpha = 0.26f)
    } else {
        scheme.outlineVariant.copy(alpha = 0.38f)
    }
    val bg = if (selected) scheme.primaryContainer.copy(alpha = 0.68f) else scheme.surfaceContainerLow
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(AppRadius.sm))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(AppRadius.sm))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = n.toString(),
            fontWeight = FontWeight.SemiBold,
            color = if (selected) scheme.onPrimaryContainer else scheme.onSurfaceVariant,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun MistakeOptionRow(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val bg = if (selected) scheme.secondaryContainer.copy(alpha = 0.48f) else scheme.surfaceContainerLow
    val border = if (selected) {
        scheme.primary.copy(alpha = 0.22f)
    } else {
        scheme.outlineVariant.copy(alpha = 0.36f)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppRadius.md))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(AppRadius.md))
            .clickable(onClick = onClick)
            .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.sm + AppSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (selected) scheme.primary else scheme.onSurfaceVariant
        )
        Spacer(Modifier.width(AppSpacing.sm + AppSpacing.xs))
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            color = scheme.onSurface,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
