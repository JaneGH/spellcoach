package com.example.spellcoach.feature.settings.presentation

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spellcoach.R
import com.example.spellcoach.data.tts.TtsAvailability
import com.example.spellcoach.domain.model.MistakeBehavior
import com.example.spellcoach.domain.model.ThemePreference
import com.example.spellcoach.core.designsystem.components.LearningCard
import com.example.spellcoach.core.designsystem.components.SpellCoachTopBar
import com.example.spellcoach.core.designsystem.theme.SpellCoachThemeExtras

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()
    val ttsAvailability by viewModel.ttsAvailability.collectAsState()
    var showResetAllConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
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
                .padding(horizontal = 16.dp)
                .padding(bottom = 88.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            if (ttsAvailability != TtsAvailability.Ready && ttsAvailability != TtsAvailability.Checking) {
                Text(
                    text = stringResource(R.string.settings_tts_unavailable),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            LearningCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.settings_theme_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.settings_theme_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = settings.themePreference == ThemePreference.SYSTEM,
                        onClick = { viewModel.setThemePreference(ThemePreference.SYSTEM) },
                        label = { Text(stringResource(R.string.settings_theme_system)) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = settings.themePreference == ThemePreference.LIGHT,
                        onClick = { viewModel.setThemePreference(ThemePreference.LIGHT) },
                        label = { Text(stringResource(R.string.settings_theme_light)) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = settings.themePreference == ThemePreference.DARK,
                        onClick = { viewModel.setThemePreference(ThemePreference.DARK) },
                        label = { Text(stringResource(R.string.settings_theme_dark)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            LearningCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = stringResource(R.string.content_desc_required_correct_icon),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.settings_required_correct_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.settings_required_correct_body),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
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

            Spacer(Modifier.height(14.dp))

            LearningCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.55f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = stringResource(R.string.content_desc_mistake_behavior_icon),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.settings_mistake_behavior_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.settings_mistake_behavior_body),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                MistakeOptionRow(
                    title = stringResource(R.string.settings_mistake_decrease),
                    icon = Icons.AutoMirrored.Filled.TrendingDown,
                    selected = settings.mistakeBehavior == MistakeBehavior.DECREASE_PROGRESS,
                    onClick = { viewModel.setMistakeBehavior(MistakeBehavior.DECREASE_PROGRESS) }
                )
                Spacer(Modifier.height(10.dp))
                MistakeOptionRow(
                    title = stringResource(R.string.settings_mistake_reset),
                    icon = Icons.Filled.Refresh,
                    selected = settings.mistakeBehavior == MistakeBehavior.RESET_PROGRESS,
                    onClick = { viewModel.setMistakeBehavior(MistakeBehavior.RESET_PROGRESS) }
                )
            }

            Spacer(Modifier.height(14.dp))

            LearningCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.settings_progress_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.settings_progress_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f))
                        .border(1.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(14.dp))
                        .clickable { showResetAllConfirm = true }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.settings_reset_all_progress),
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

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
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = stringResource(R.string.content_desc_audio_icon),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.settings_audio_title),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(R.string.settings_audio_body),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = settings.audioEnabled,
                        onCheckedChange = viewModel::setAudioEnabled,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            LearningCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.settings_letter_hints_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.settings_letter_hints_body),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = settings.letterHintsEnabled,
                        onCheckedChange = viewModel::setLetterHintsEnabled,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            LearningCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.settings_speech_rate_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.settings_speech_rate_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Slider(
                    value = settings.speechRate,
                    onValueChange = viewModel::setSpeechRate,
                    valueRange = 0.5f..2f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = stringResource(R.string.speech_rate_format, settings.speechRate),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(14.dp))

            LearningCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.settings_reward_sounds_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.settings_reward_sounds_body),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = settings.rewardSoundsEnabled,
                        onCheckedChange = viewModel::setRewardSounds,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.settings_animations_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.settings_animations_body),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = settings.animationsEnabled,
                        onCheckedChange = viewModel::setAnimations,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.72f))
                    .clickable { viewModel.openTtsSettings() }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.settings_open_tts),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(SpellCoachThemeExtras.current.mascotPanel),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = stringResource(R.string.settings_mascot_quote),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )

                Image(
                    painter = painterResource(R.drawable.fox_happy2),
                    contentDescription = stringResource(R.string.content_desc_settings_mascot),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 8.dp, bottom = 6.dp)
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
    val borderColor = if (selected) scheme.primary else scheme.outline
    val bg = if (selected) scheme.primaryContainer else scheme.surface
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(2.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = n.toString(),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
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
    val bg = if (selected) scheme.primaryContainer else scheme.surface
    val border = if (selected) scheme.primary else scheme.outlineVariant
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(2.dp, border, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(10.dp))
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
