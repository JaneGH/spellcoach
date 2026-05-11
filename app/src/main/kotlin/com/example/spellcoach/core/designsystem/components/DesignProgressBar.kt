package com.example.spellcoach.core.designsystem.components

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
import com.example.spellcoach.core.designsystem.theme.SpellCoachThemeExtras
import com.example.spellcoach.core.designsystem.tokens.AppDimensions
import com.example.spellcoach.core.designsystem.tokens.AppRadius

@Composable
fun DesignProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = AppDimensions.progressBarTrackHeight,
    trackColor: Color = SpellCoachThemeExtras.current.progressTrack,
    fillColor: Color = SpellCoachThemeExtras.current.progressFill,
    fullMastered: Boolean = false
) {
    val masteredColor = SpellCoachThemeExtras.current.progressMastered
    val p = progress.coerceIn(0f, 1f)
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(AppRadius.pill))
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
                color = if (fullMastered) masteredColor else fillColor,
                size = Size(fillW, h),
                cornerRadius = CornerRadius(h / 2, h / 2)
            )
        }
    }
}
