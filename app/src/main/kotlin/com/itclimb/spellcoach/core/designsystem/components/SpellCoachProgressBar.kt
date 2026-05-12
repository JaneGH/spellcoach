package com.itclimb.spellcoach.core.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import com.itclimb.spellcoach.core.designsystem.motion.SpellCoachMotion
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import com.itclimb.spellcoach.core.designsystem.theme.SpellCoachThemeExtras
import com.itclimb.spellcoach.core.designsystem.tokens.AppDimensions

/**
 * Premium, slim progress bar shared across cards and inline progress.
 *
 * - Thinner default height than the Material default for a refined feel.
 * - Pill-rounded track and fill.
 * - Subtle tonal track (blends [progressTrack] with the surface) so it never feels stark.
 * - Smooth animated progress.
 * - Optional [fullMastered] swap to a calmer "mastered" tone.
 */
@Composable
fun SpellCoachProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = AppDimensions.progressBarTrackSlim,
    trackColor: Color? = null,
    fillColor: Color? = null,
    fullMastered: Boolean = false,
    animate: Boolean = true
) {
    val scheme = MaterialTheme.colorScheme
    val extras = SpellCoachThemeExtras.current
    val isLight = scheme.background.luminance() > 0.5f

    val resolvedTrack = trackColor ?: lerp(
        extras.progressTrack,
        scheme.surface,
        if (isLight) 0.45f else 0.18f
    )
    val resolvedFill = fillColor ?: if (fullMastered) extras.progressMastered else extras.progressFill

    val target = progress.coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = target,
        animationSpec = SpellCoachMotion.gentleSpring(),
        label = "spellcoach_progress"
    )
    val displayed = if (animate) animated else target

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(percent = 50))
    ) {
        val w = size.width
        val h = size.height
        val radius = CornerRadius(h / 2, h / 2)
        drawRoundRect(
            color = resolvedTrack,
            size = Size(w, h),
            cornerRadius = radius
        )
        val fillW = w * displayed
        if (fillW > 0f) {
            drawRoundRect(
                color = resolvedFill,
                size = Size(fillW, h),
                cornerRadius = radius
            )
        }
    }
}
