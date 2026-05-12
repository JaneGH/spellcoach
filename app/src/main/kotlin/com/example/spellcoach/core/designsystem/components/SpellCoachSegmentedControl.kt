package com.example.spellcoach.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.spellcoach.core.designsystem.tokens.AppDimensions
import com.example.spellcoach.core.designsystem.tokens.AppRadius
import com.example.spellcoach.core.designsystem.tokens.AppSpacing

data class SegmentedOption(
    val title: String,
    val icon: ImageVector
)

@Composable
fun SpellCoachSegmentedControl(
    options: List<SegmentedOption>,
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val trackShape = RoundedCornerShape(AppRadius.pill)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(AppDimensions.buttonHeightLarge)
            .clip(trackShape)
            .background(scheme.surfaceVariant.copy(alpha = 0.45f))
            .border(
                width = 1.dp,
                color = scheme.outlineVariant.copy(alpha = 0.55f),
                shape = trackShape
            )
            .padding(AppSpacing.xs),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEachIndexed { idx, option ->
            val selected = idx == selectedIndex
            val pillShape = RoundedCornerShape(AppRadius.pill)
            val bg = if (selected) scheme.primaryContainer else scheme.surface.copy(alpha = 0f)
            val border = if (selected) scheme.primary.copy(alpha = 0.35f) else scheme.outlineVariant.copy(alpha = 0f)
            val content = if (selected) scheme.primary else scheme.onSurfaceVariant
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(AppDimensions.buttonHeightLarge - AppSpacing.sm)
                    .clip(pillShape)
                    .background(bg)
                    .border(1.dp, border, pillShape)
                    .clickable { onSelectIndex(idx) },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = option.icon,
                        contentDescription = null,
                        tint = content,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(AppSpacing.sm))
                    Text(
                        text = option.title,
                        color = content,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
