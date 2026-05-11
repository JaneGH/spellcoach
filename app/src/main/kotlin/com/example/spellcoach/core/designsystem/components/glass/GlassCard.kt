package com.example.spellcoach.core.designsystem.components.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 34.dp,
    contentPadding: PaddingValues = PaddingValues(18.dp),
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .shadow(
                elevation = 34.dp,
                shape = shape,
                ambientColor = Color(0xFF38BDF8).copy(alpha = 0.30f),
                spotColor = Color(0xFF0F172A).copy(alpha = 0.16f),
                clip = false
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.86f),
                        Color(0xFFF4FBFF).copy(alpha = 0.60f),
                        Color(0xFFD4ECFF).copy(alpha = 0.46f)
                    )
                )
            )
            .border(
                width = 1.7.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 1f),
                        Color.White.copy(alpha = 0.54f),
                        Color(0xFF7DD3FC).copy(alpha = 0.36f)
                    )
                ),
                shape = shape
            )
    ) {

        // glossy top highlight
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.58f),
                            Color.White.copy(alpha = 0.18f),
                            Color.Transparent
                        ),
                        center = Offset(120f, 70f),
                        radius = 520f
                    )
                )
        )

        // subtle bottom tint
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF38BDF8).copy(alpha = 0.12f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier.padding(contentPadding)
        ) {
            content()
        }
    }
}