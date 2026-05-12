package com.itclimb.spellcoach.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import com.itclimb.spellcoach.core.designsystem.tokens.AppSpacing

fun Modifier.spellCoachScreenHorizontalPadding(): Modifier =
    padding(horizontal = AppSpacing.screenHorizontal)

@Composable
fun SpellCoachScreenContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val isLight = scheme.background.luminance() > 0.5f
    val top = lerp(
        scheme.background,
        scheme.primaryContainer,
        if (isLight) 0.055f else 0.07f
    )
    val mid = scheme.background
    val bottom = lerp(
        scheme.background,
        scheme.secondaryContainer,
        if (isLight) 0.04f else 0.05f
    )
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(top, mid, bottom),
                    startY = 0f,
                    endY = 1600f
                )
            ),
        content = content
    )
}

@Composable
fun SpellCoachTopHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    SpellCoachHeader(title = title, modifier = modifier, subtitle = subtitle)
}

/** Screen title block: bold title, optional subtitle in [onSurfaceVariant]. */
@Composable
fun SpellCoachHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    Column(
        modifier = modifier
            .spellCoachScreenHorizontalPadding()
            .padding(
                top = AppSpacing.lg,
                bottom = AppSpacing.lg
            )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (subtitle != null) {
            Spacer(Modifier.height(AppSpacing.xs + AppSpacing.xxs))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.86f)
            )
        }
    }
}
