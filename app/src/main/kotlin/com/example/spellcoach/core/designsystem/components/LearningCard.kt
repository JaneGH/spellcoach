package com.example.spellcoach.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.spellcoach.core.designsystem.tokens.AppElevation
import com.example.spellcoach.core.designsystem.tokens.AppRadius
import com.example.spellcoach.core.designsystem.tokens.AppSpacing

@Composable
fun LearningCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(AppRadius.card),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.level2)
    ) {
        Column(Modifier.padding(AppSpacing.lg)) {
            content()
        }
    }
}
