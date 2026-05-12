package com.example.spellcoach.core.designsystem.components

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
import com.example.spellcoach.core.designsystem.tokens.AppElevation
import com.example.spellcoach.core.designsystem.tokens.AppRadius
import com.example.spellcoach.core.designsystem.tokens.AppSpacing

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
    val container = if (isLight) {
        lerp(scheme.surface, scheme.primaryContainer, 0.025f)
    } else {
        lerp(scheme.surface, scheme.primaryContainer, 0.05f)
    }

    val borderAlpha = if (isLight) 0.06f else 0.10f

    Card(
        modifier = modifier.clip(shape),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = container,
            contentColor = scheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = AppElevation.level2,
            pressedElevation = AppElevation.level1,
            focusedElevation = AppElevation.level2,
            hoveredElevation = AppElevation.level2,
            draggedElevation = AppElevation.level2,
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
                .padding(AppSpacing.lg),
            content = content
        )
    }
}
