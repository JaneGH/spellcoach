package com.example.spellcoach.core.designsystem.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
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
    val isLight = scheme.background.luminance() > 0.5f
    val trackShape = RoundedCornerShape(AppRadius.pill)
    val trackHeight = 36.dp
    val segmentHeight = 28.dp
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(trackHeight)
            .clip(trackShape)
            .background(
                if (isLight) {
                    scheme.surfaceVariant.copy(alpha = 0.4f)
                } else {
                    scheme.surfaceVariant.copy(alpha = 0.36f)
                }
            )
            .padding(horizontal = AppSpacing.xs, vertical = AppSpacing.xxs),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEachIndexed { idx, option ->
            val selected = idx == selectedIndex
            val pillShape = RoundedCornerShape(AppRadius.pill)
            val bg = if (selected) {
                scheme.secondaryContainer.copy(alpha = if (isLight) 0.88f else 0.72f)
            } else {
                scheme.surface.copy(alpha = 0f)
            }
            val content = if (selected) {
                scheme.onSecondaryContainer
            } else {
                scheme.onSurfaceVariant
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(segmentHeight)
                    .clip(pillShape)
                    .background(bg)
                    .clickable { onSelectIndex(idx) },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = AppSpacing.sm)
                ) {
                    Icon(
                        imageVector = option.icon,
                        contentDescription = null,
                        tint = content,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(Modifier.width(AppSpacing.sm))
                    Text(
                        text = option.title,
                        color = content,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
