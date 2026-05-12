package com.example.spellcoach.feature.results.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spellcoach.R
import com.example.spellcoach.core.designsystem.components.DesignProgressBar
import com.example.spellcoach.core.designsystem.components.LearningCard
import com.example.spellcoach.core.designsystem.components.SpellCoachSecondaryButton
import com.example.spellcoach.core.designsystem.components.SpellCoachPrimaryButton
import com.example.spellcoach.core.designsystem.components.SpellCoachScreenContainer
import com.example.spellcoach.core.designsystem.components.SpellCoachTopBar
import com.example.spellcoach.core.designsystem.components.spellCoachScreenHorizontalPadding
import com.example.spellcoach.core.designsystem.theme.SpellCoachThemeExtras
import com.example.spellcoach.core.designsystem.tokens.AppDimensions
import com.example.spellcoach.core.designsystem.tokens.AppRadius
import com.example.spellcoach.core.designsystem.tokens.AppSpacing

@Composable
fun ResultsScreen(
    onBack: () -> Unit,
    onPracticeAgain: (Long) -> Unit,
    onGoToLists: () -> Unit,
    viewModel: ResultsViewModel = hiltViewModel()
) {
    val result = viewModel.result
    val toPractice = result?.let { (it.total - it.correct).coerceAtLeast(0) } ?: 0
    val scheme = MaterialTheme.colorScheme
    val extras = SpellCoachThemeExtras.current

    SpellCoachScreenContainer {
        SpellCoachTopBar(
            showBack = true,
            onBack = onBack,
            brandTitle = "SpellCoach",
            brandAccent = null,
            screenTitle = null,
            subtitleBelowBrand = null
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .spellCoachScreenHorizontalPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))
            Box(
                modifier = Modifier
                    .size(AppDimensions.resultsHeroImage)
                    .clip(RoundedCornerShape(AppRadius.lg))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_results),
                    contentDescription = null
                )
            }
            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))
            Text(
                text = "Good job!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = scheme.onSurface
            )
            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))
            Text(
                text = "You worked hard and learned\nsome tricky words today.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = scheme.onSurfaceVariant
            )

            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.md))

            LearningCard(modifier = Modifier.fillMaxWidth()) {
                val correct = result?.correct ?: 0
                val total = result?.total ?: 0
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$correct",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        color = extras.success
                    )
                    Text(
                        text = " / $total",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = scheme.onSurface,
                        modifier = Modifier.padding(start = AppSpacing.xs)
                    )
                }
                Spacer(Modifier.height(AppSpacing.sm))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AppRadius.pill))
                            .background(extras.success.copy(alpha = 0.15f))
                            .padding(horizontal = AppSpacing.sm + AppSpacing.xs, vertical = AppSpacing.sm + AppSpacing.xs)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = extras.success,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "  Words Correct",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = extras.onSuccessContainer
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))

            LearningCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$toPractice",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = scheme.tertiary
                    )
                }
                Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AppRadius.pill))
                            .background(scheme.tertiaryContainer.copy(alpha = 0.55f))
                            .padding(horizontal = AppSpacing.sm + AppSpacing.xs, vertical = AppSpacing.sm + AppSpacing.xs)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = scheme.tertiary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "  To Practice",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = scheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(AppSpacing.md))

            LearningCard(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = "Goal",
                        fontWeight = FontWeight.Bold,
                        color = scheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "80%",
                        fontWeight = FontWeight.Bold,
                        color = extras.success,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))
                DesignProgressBar(progress = 0.8f, fullMastered = false)
                Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))
                Text(
                    text = "Just 2 more sessions to reach your\ndiamond badge!",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = scheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.md))

            SpellCoachPrimaryButton(
                text = "Practice Again",
                onClick = {
                    val id = result?.listId
                    if (id != null) onPracticeAgain(id)
                },
                leadingIcon = Icons.Filled.Refresh
            )
            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))
            SpellCoachSecondaryButton(
                text = "Go to Lists",
                onClick = onGoToLists,
                leadingIcon = Icons.AutoMirrored.Filled.List
            )
            Spacer(Modifier.height(AppSpacing.xl))
        }
    }
}
