package com.example.spellcoach.presentation.components.glass

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GlassSegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 999.dp
) {
    val shape = RoundedCornerShape(cornerRadius)

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
        options.forEachIndexed { idx, label ->
            val selected = idx == selectedIndex

            val scale by animateFloatAsState(
                targetValue = if (selected) 1f else 0.97f,
                label = "seg_scale"
            )

            val textAlpha by animateFloatAsState(
                targetValue = if (selected) 1f else 0.62f,
                label = "seg_text_alpha"
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
                                elevation = 10.dp,
                                shape = pillShape,
                                clip = false,
                                ambientColor = Color(0xFF38BDF8).copy(alpha = 0.22f),
                                spotColor = Color(0xFF38BDF8).copy(alpha = 0.22f)
                            )
                        } else {
                            Modifier
                        }
                    )
                    .clip(pillShape)
                    .background(
                        if (selected) {
                            Brush.horizontalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.92f),
                                    Color(0xFFDFF3FF).copy(alpha = 0.88f)
                                )
                            )
                        } else {
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, Color.Transparent)
                            )
                        }
                    )
                    .border(
                        width = if (selected) 1.dp else 0.dp,
                        color = if (selected) Color(0xFF38BDF8).copy(alpha = 0.30f) else Color.Transparent,
                        shape = pillShape
                    )
                    .clickable { onSelectIndex(idx) },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (selected) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color(0xFF0B6B8C),
                            modifier = Modifier.padding(end = 7.dp)
                        )
                    }

                    Text(
                        text = label,
                        color = Color(0xFF0B6B8C).copy(alpha = textAlpha),
                        fontSize = 14.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}