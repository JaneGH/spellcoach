package com.example.spellcoach.core.designsystem.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LearningCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    SpellCoachCard(modifier = modifier, content = content)
}
