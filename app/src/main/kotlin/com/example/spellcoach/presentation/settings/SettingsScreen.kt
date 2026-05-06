package com.example.spellcoach.presentation.settings

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spellcoach.data.tts.TtsAvailability
import com.example.spellcoach.domain.model.MistakeBehavior
import com.example.spellcoach.presentation.components.LearningCard
import com.example.spellcoach.presentation.components.SpellCoachTopBar
import com.example.spellcoach.presentation.theme.PrimaryBlue
import java.util.Locale

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()
    val ttsAvailability by viewModel.ttsAvailability.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FBFF))
    ) {
        SpellCoachTopBar(
            showBack = false,
            onBack = {},
            brandTitle = "SpellCoach",
            brandAccent = null,
            screenTitle = null,
            heroTitle = "Settings",
            subtitleBelowBrand = "Customize your learning experience to match your pace."
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
                    text = "Speech isn’t available on this device yet. You can open system settings to install or enable text‑to‑speech.",
                    color = Color(0xFFB45309),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            LearningCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFE3F2FD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = PrimaryBlue)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Required correct answers",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1C1E)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "How many times should you spell a word correctly to master it?",
                            color = Color(0xFF44474E),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
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
                            .background(Color(0xFFF3E8FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.TrendingDown, contentDescription = null, tint = Color(0xFF7E22CE))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Mistake behavior",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1C1E)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "What happens when you misspell a word?",
                            color = Color(0xFF44474E),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                MistakeOptionRow(
                    title = "Decrease progress",
                    icon = Icons.AutoMirrored.Filled.TrendingDown,
                    selected = settings.mistakeBehavior == MistakeBehavior.DECREASE_PROGRESS,
                    onClick = { viewModel.setMistakeBehavior(MistakeBehavior.DECREASE_PROGRESS) }
                )
                Spacer(Modifier.height(10.dp))
                MistakeOptionRow(
                    title = "Reset progress",
                    icon = Icons.Filled.Refresh,
                    selected = settings.mistakeBehavior == MistakeBehavior.RESET_PROGRESS,
                    onClick = { viewModel.setMistakeBehavior(MistakeBehavior.RESET_PROGRESS) }
                )
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
                                .background(Color(0xFFD1FAE5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = Color(0xFF166534))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Audio Pronunciation",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1C1E)
                            )
                            Text(
                                text = "Read words aloud automatically.",
                                color = Color(0xFF44474E),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Switch(
                        checked = settings.audioEnabled,
                        onCheckedChange = viewModel::setAudioEnabled,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF2E7D32)
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
                            text = "Enable Letter Hints",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1C1E)
                        )
                        Text(
                            text = "Show optional letter chips during practice.",
                            color = Color(0xFF44474E),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Switch(
                        checked = settings.letterHintsEnabled,
                        onCheckedChange = viewModel::setLetterHintsEnabled,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF2E7D32)
                        )
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            LearningCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Speech rate",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Adjust how fast words are spoken.",
                    color = Color(0xFF44474E),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Slider(
                    value = settings.speechRate,
                    onValueChange = viewModel::setSpeechRate,
                    valueRange = 0.5f..2f,
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryBlue,
                        activeTrackColor = PrimaryBlue
                    )
                )
                Text(
                    text = String.format(Locale.US, "%.1f×", settings.speechRate),
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                    fontSize = 14.sp
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
                            text = "Reward sounds",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1C1E)
                        )
                        Text(
                            text = "Play sounds for success and completion.",
                            color = Color(0xFF44474E),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Switch(
                        checked = settings.rewardSoundsEnabled,
                        onCheckedChange = viewModel::setRewardSounds,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF2E7D32)
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
                            text = "Animations",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1C1E)
                        )
                        Text(
                            text = "Celebrate answers with gentle motion.",
                            color = Color(0xFF44474E),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Switch(
                        checked = settings.animationsEnabled,
                        onCheckedChange = viewModel::setAnimations,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF2E7D32)
                        )
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF0B6B8C))
                    .clickable { viewModel.openTtsSettings() }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Open text‑to‑speech settings",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF7EC8E3)),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = "Every mistake is a new chance\nto learn better!",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun NumberPickCell(
    n: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) PrimaryBlue else Color(0xFFB6DDEF)
    val bg = if (selected) Color(0xFFE3F2FD) else Color.White
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
            color = PrimaryBlue,
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
    val bg = if (selected) Color(0xFFE3F2FD) else Color.White
    val border = if (selected) PrimaryBlue else Color(0xFFE2E8F0)
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
        Icon(icon, contentDescription = null, tint = PrimaryBlue)
        Spacer(Modifier.width(10.dp))
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1C1E),
            fontSize = 15.sp
        )
    }
}
