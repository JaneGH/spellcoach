package com.example.spellcoach.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class SpellCoachExtras(
    val gradientStart: Color,
    val gradientEnd: Color,
    val glassBlobCyan: Color,
    val glassBlobViolet: Color,
    val glassBlobTeal: Color,
    val success: Color,
    val onSuccessContainer: Color,
    val positiveAction: Color,
    val chipBlueBg: Color,
    val chipBlueFg: Color,
    val chipPurpleBg: Color,
    val chipPurpleFg: Color,
    val progressTrack: Color,
    val progressFill: Color,
    val progressMastered: Color,
    val mascotPanel: Color
)

private val LocalSpellCoachExtras = staticCompositionLocalOf {
    SpellCoachExtras(
        gradientStart = Color.Unspecified,
        gradientEnd = Color.Unspecified,
        glassBlobCyan = Color.Unspecified,
        glassBlobViolet = Color.Unspecified,
        glassBlobTeal = Color.Unspecified,
        success = Color.Unspecified,
        onSuccessContainer = Color.Unspecified,
        positiveAction = Color.Unspecified,
        chipBlueBg = Color.Unspecified,
        chipBlueFg = Color.Unspecified,
        chipPurpleBg = Color.Unspecified,
        chipPurpleFg = Color.Unspecified,
        progressTrack = Color.Unspecified,
        progressFill = Color.Unspecified,
        progressMastered = Color.Unspecified,
        mascotPanel = Color.Unspecified
    )
}

private val LightExtras = SpellCoachExtras(
    gradientStart = Color(0xFF0B6B8C),
    gradientEnd = Color(0xFF22D3EE),
    glassBlobCyan = Color(0xFF7DD3FC),
    glassBlobViolet = Color(0xFFA78BFA),
    glassBlobTeal = Color(0xFF22D3EE),
    success = SpellCoachSemantic.successLight,
    onSuccessContainer = Color(0xFF166534),
    positiveAction = Color(0xFF006D3B),
    chipBlueBg = Color(0xFFE0F2FE),
    chipBlueFg = Color(0xFF0369A1),
    chipPurpleBg = Color(0xFFF3E8FF),
    chipPurpleFg = Color(0xFF7E22CE),
    // Calm tonal track + on-palette success fill (less neon than the previous bright green).
    progressTrack = Color(0xFFE3ECF3),
    progressFill = Color(0xFF16A34A),
    progressMastered = Color(0xFF15803D),
    mascotPanel = Color(0xFF7EC8E3)
)

private val DarkExtras = SpellCoachExtras(
    gradientStart = Color(0xFF0E7490),
    gradientEnd = Color(0xFF38BDF8),
    glassBlobCyan = Color(0xFF38BDF8),
    glassBlobViolet = Color(0xFFC4B5FD),
    glassBlobTeal = Color(0xFF2DD4BF),
    success = SpellCoachSemantic.successDark,
    onSuccessContainer = Color(0xFFBBF7D0),
    positiveAction = Color(0xFF34D399),
    chipBlueBg = Color(0xFF1E3A5F),
    chipBlueFg = Color(0xFFBAE6FD),
    chipPurpleBg = Color(0xFF3B255C),
    chipPurpleFg = Color(0xFFE9D5FF),
    progressTrack = Color(0xFF22344A),
    progressFill = Color(0xFF34D399),
    progressMastered = Color(0xFF6EE7B7),
    mascotPanel = Color(0xFF1A4A63)
)

object SpellCoachThemeExtras {
    val current: SpellCoachExtras
        @Composable
        @ReadOnlyComposable
        get() = LocalSpellCoachExtras.current
}

@Composable
fun SpellCoachTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SpellCoachDarkColorScheme else SpellCoachLightColorScheme
    val extras = if (darkTheme) DarkExtras else LightExtras
    CompositionLocalProvider(LocalSpellCoachExtras provides extras) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = SpellCoachTypography,
            shapes = AppShapes,
            content = content
        )
    }
}
