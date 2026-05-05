package com.example.spellcoach.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = PillSelectedBg,
    secondary = PrimaryBlueStrong,
    background = ScreenBackground,
    surface = CardWhite,
    onBackground = TitleDark,
    onSurface = TitleDark,
    outline = DashedBorder
)

@Composable
fun SpellCoachTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = SpellCoachTypography,
        content = content
    )
}
