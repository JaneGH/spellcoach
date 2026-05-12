package com.example.spellcoach.core.designsystem.components

import androidx.compose.foundation.border
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
    Card(
        modifier = modifier
            .clip(shape)
            .border(
                width = 1.dp,
                color = scheme.outlineVariant.copy(alpha = 0.55f),
                shape = shape
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = scheme.surface,
            contentColor = scheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.level1)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(AppSpacing.lg),
            content = content
        )
    }
}
