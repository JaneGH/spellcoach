package com.example.spellcoach.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.spellcoach.presentation.theme.ProgressFill
import com.example.spellcoach.presentation.theme.ProgressTrack

@Composable
fun DesignProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 10.dp,
    trackColor: Color = ProgressTrack,
    fillColor: Color = ProgressFill,
    fullMastered: Boolean = false
) {
    val p = progress.coerceIn(0f, 1f)
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(50))
    ) {
        val w = size.width
        val h = size.height
        drawRoundRect(
            color = trackColor,
            size = Size(w, h),
            cornerRadius = CornerRadius(h / 2, h / 2)
        )
        val fillW = w * p
        if (fillW > 0f) {
            drawRoundRect(
                color = if (fullMastered) Color(0xFF15803D) else fillColor,
                size = Size(fillW, h),
                cornerRadius = CornerRadius(h / 2, h / 2)
            )
        }
    }
}
