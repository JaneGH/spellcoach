package com.itclimb.spellcoach.feature.results.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itclimb.spellcoach.R
import com.itclimb.spellcoach.core.designsystem.components.LearningCard
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachProgressBar
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachSecondaryButton
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachPrimaryButton
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachScreenContainer
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachTopBar
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachTopBarVariant
import com.itclimb.spellcoach.core.designsystem.components.spellCoachScreenHorizontalPadding
import com.itclimb.spellcoach.core.designsystem.theme.SpellCoachThemeExtras
import com.itclimb.spellcoach.core.designsystem.tokens.AppDimensions
import com.itclimb.spellcoach.core.designsystem.tokens.AppIconSize
import com.itclimb.spellcoach.core.designsystem.tokens.AppRadius
import com.itclimb.spellcoach.core.designsystem.tokens.AppSpacing

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
            variant = SpellCoachTopBarVariant.Inner,
            onBack = onBack,
            innerTitle = stringResource(R.string.results_nav_title)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .spellCoachScreenHorizontalPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(AppSpacing.md))
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val heroSide =
                    (maxWidth * AppDimensions.resultsHeroImageWidthFraction).coerceIn(
                        AppDimensions.resultsHeroImageMin,
                        AppDimensions.resultsHeroImageMax
                    )
                Box(
                    modifier = Modifier
                        .size(heroSide)
                        .clip(RoundedCornerShape(AppRadius.lg))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_results),
                        contentDescription = stringResource(R.string.content_desc_results_illustration)
                    )
                }
            }
            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))
            Text(
                text = stringResource(R.string.results_headline),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = scheme.onSurface
            )
            Spacer(Modifier.height(AppSpacing.xs + AppSpacing.xxs))
            Text(
                text = stringResource(R.string.results_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.onSurfaceVariant.copy(alpha = 0.78f)
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
                        text = stringResource(R.string.results_score_format, correct),
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        color = extras.success
                    )
                    Text(
                        text = stringResource(R.string.results_score_total_format, total),
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
                                modifier = Modifier.size(AppIconSize.sm)
                            )
                            Spacer(Modifier.width(AppSpacing.sm))
                            Text(
                                text = stringResource(R.string.results_words_correct),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
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
                        text = stringResource(R.string.results_score_format, toPractice),
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
                                modifier = Modifier.size(AppIconSize.sm)
                            )
                            Spacer(Modifier.width(AppSpacing.sm))
                            Text(
                                text = stringResource(R.string.results_to_practice),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
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
                        text = stringResource(R.string.results_goal_label),
                        fontWeight = FontWeight.SemiBold,
                        color = scheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.results_goal_value),
                        fontWeight = FontWeight.SemiBold,
                        color = extras.success,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))
                SpellCoachProgressBar(progress = 0.8f, fullMastered = false)
                Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))
                Text(
                    text = stringResource(R.string.results_goal_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant.copy(alpha = 0.78f)
                )
            }

            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.md))

            SpellCoachPrimaryButton(
                text = stringResource(R.string.results_practice_again),
                onClick = {
                    val id = result?.listId
                    if (id != null) onPracticeAgain(id)
                },
                leadingIcon = Icons.Filled.Refresh
            )
            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))
            SpellCoachSecondaryButton(
                text = stringResource(R.string.results_go_to_lists),
                onClick = onGoToLists,
                leadingIcon = Icons.AutoMirrored.Filled.List
            )
            Spacer(Modifier.height(AppSpacing.xl))
        }
    }
}
