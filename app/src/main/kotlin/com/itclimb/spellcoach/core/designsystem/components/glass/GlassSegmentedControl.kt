package com.itclimb.spellcoach.core.designsystem.components.glass

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itclimb.spellcoach.core.designsystem.components.SegmentedOption

private val SegmentedAnimationSpecFloat = tween<Float>(durationMillis = 220, easing = FastOutSlowInEasing)
private val SegmentedAnimationSpecColor = tween<Color>(durationMillis = 220, easing = FastOutSlowInEasing)

@Composable
fun GlassSegmentedControl(
    options: List<SegmentedOption>,
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 999.dp
) {
    val shape = RoundedCornerShape(cornerRadius)
    val accent = Color(0xFF0B6B8C)
    val glowCyan = Color(0xFF38BDF8)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.72f),
                        Color(0xFFEAF6FF).copy(alpha = 0.52f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.85f),
                shape = shape
            )
            .padding(5.dp)
    ) {
        options.forEachIndexed { idx, option ->
            val selected = idx == selectedIndex

            val scale by animateFloatAsState(
                targetValue = if (selected) 1f else 0.97f,
                animationSpec = SegmentedAnimationSpecFloat,
                label = "seg_scale"
            )

            val contentColor by animateColorAsState(
                targetValue = if (selected) accent else accent.copy(alpha = 0.55f),
                animationSpec = SegmentedAnimationSpecColor,
                label = "seg_content"
            )

            val pillBgStart by animateColorAsState(
                targetValue = if (selected) Color.White.copy(alpha = 0.94f) else Color.White.copy(alpha = 0f),
                animationSpec = SegmentedAnimationSpecColor,
                label = "seg_pill_bg_start"
            )

            val pillBgEnd by animateColorAsState(
                targetValue = if (selected) Color(0xFFDFF3FF).copy(alpha = 0.92f) else Color.White.copy(alpha = 0f),
                animationSpec = SegmentedAnimationSpecColor,
                label = "seg_pill_bg_end"
            )

            val pillBorder by animateColorAsState(
                targetValue = if (selected) glowCyan.copy(alpha = 0.42f) else Color.Transparent,
                animationSpec = SegmentedAnimationSpecColor,
                label = "seg_pill_border"
            )

            val pillShape = RoundedCornerShape(999.dp)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .then(
                        if (selected) {
                            Modifier.shadow(
                                elevation = 12.dp,
                                shape = pillShape,
                                clip = false,
                                ambientColor = glowCyan.copy(alpha = 0.28f),
                                spotColor = glowCyan.copy(alpha = 0.32f)
                            )
                        } else {
                            Modifier
                        }
                    )
                    .clip(pillShape)
                    .background(Brush.horizontalGradient(listOf(pillBgStart, pillBgEnd)))
                    .border(
                        width = 1.dp,
                        color = pillBorder,
                        shape = pillShape
                    )
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
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = option.title,
                        color = contentColor,
                        fontSize = 14.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
