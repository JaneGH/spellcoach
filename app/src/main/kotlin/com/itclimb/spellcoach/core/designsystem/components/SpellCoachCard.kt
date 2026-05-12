package com.itclimb.spellcoach.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.itclimb.spellcoach.core.designsystem.tokens.AppElevation
import com.itclimb.spellcoach.core.designsystem.tokens.AppRadius
import com.itclimb.spellcoach.core.designsystem.tokens.AppSpacing

@Composable
fun SpellCoachCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(AppRadius.card)
    val isLight = scheme.background.luminance() > 0.5f

    // Subtly tint the card toward primaryContainer so it lifts off the background
    // without feeling stark, but still keeps depth via elevation.
    val baseSurface = scheme.surfaceContainerLow
    val container = if (isLight) {
        lerp(baseSurface, scheme.primaryContainer, 0.04f)
    } else {
        lerp(baseSurface, scheme.primaryContainer, 0.07f)
    }

    val borderAlpha = if (isLight) 0.08f else 0.12f

    Card(
        modifier = modifier.clip(shape),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = container,
            contentColor = scheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = AppElevation.level3,
            pressedElevation = AppElevation.level2,
            focusedElevation = AppElevation.level3,
            hoveredElevation = AppElevation.level3,
            draggedElevation = AppElevation.level3,
            disabledElevation = AppElevation.level0
        ),
        border = BorderStroke(
            width = 1.dp,
            color = scheme.outlineVariant.copy(alpha = borderAlpha)
        )
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(AppSpacing.cardContentPadding),
            content = content
        )
    }
}
