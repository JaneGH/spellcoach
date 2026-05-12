package com.example.spellcoach.core.designsystem.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.spellcoach.core.designsystem.motion.SpellCoachMotion
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
    val trackHeight = 38.dp
    val segmentHeight = 30.dp
    val trackColor = if (isLight) {
        lerp(scheme.surfaceVariant, scheme.surface, 0.5f)
    } else {
        scheme.surfaceVariant.copy(alpha = 0.32f)
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(trackHeight)
            .clip(trackShape)
            .background(trackColor)
            .padding(horizontal = AppSpacing.xs, vertical = AppSpacing.xxs),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEachIndexed { idx, option ->
            val selected = idx == selectedIndex
            val pillShape = RoundedCornerShape(AppRadius.pill)
            val targetBg = if (selected) {
                if (isLight) {
                    scheme.surface
                } else {
                    lerp(scheme.surface, scheme.surfaceVariant, 0.4f)
                }
            } else {
                Color.Transparent
            }
            val targetContent = if (selected) {
                scheme.onSurface
            } else {
                scheme.onSurfaceVariant.copy(alpha = 0.78f)
            }
            val animatedBg by animateColorAsState(
                targetValue = targetBg,
                animationSpec = SpellCoachMotion.gentleSpring(),
                label = "segment_bg"
            )
            val animatedContent by animateColorAsState(
                targetValue = targetContent,
                animationSpec = SpellCoachMotion.gentleSpring(),
                label = "segment_content"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(segmentHeight)
                    .clip(pillShape)
                    .background(animatedBg)
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
                        tint = animatedContent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(AppSpacing.sm))
                    Text(
                        text = option.title,
                        color = animatedContent,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
