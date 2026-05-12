package com.example.spellcoach.core.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.example.spellcoach.core.designsystem.tokens.AppDimensions

/**
 * Backwards-compatible alias for [SpellCoachProgressBar].
 * Prefer [SpellCoachProgressBar] in new code.
 */
@Composable
fun DesignProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = AppDimensions.progressBarTrackSlim,
    trackColor: Color? = null,
    fillColor: Color? = null,
    fullMastered: Boolean = false
) {
    SpellCoachProgressBar(
        progress = progress,
        modifier = modifier,
        height = height,
        trackColor = trackColor,
        fillColor = fillColor,
        fullMastered = fullMastered
    )
}
