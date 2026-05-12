package com.itclimb.spellcoach.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val BluePrimary = Color(0xFF127399)
private val BlueOnPrimary = Color(0xFFFFFFFF)
private val BluePrimaryDark = Color(0xFF58B4E8)
private val BlueOnPrimaryDark = Color(0xFF002231)

private val BlueSecondary = Color(0xFF00668B)
private val BlueSecondaryDark = Color(0xFF89D7F0)

private val PurpleTertiary = Color(0xFF7E22CE)
private val PurpleTertiaryDark = Color(0xFFD8B4FF)

/** Slight cool tint so screens read less flat than pure gray; tuned for soft lift off cards. */
private val LightBackground = Color(0xFFE9EEF5)
private val LightSurface = Color(0xFFFCFCFD)
private val LightSurfaceVariant = Color(0xFFE1E8F0)

private val LightSurfaceContainerLow = Color(0xFFF4F6FA)
private val LightSurfaceContainer = Color(0xFFEEF1F7)
private val LightSurfaceContainerHigh = Color(0xFFE8EDF4)
private val LightSurfaceContainerHighest = Color(0xFFE2E8F0)

private val DarkBackground = Color(0xFF0A121C)
private val DarkSurface = Color(0xFF131E2C)
private val DarkSurfaceVariant = Color(0xFF1F2D3F)

private val DarkSurfaceContainerLow = Color(0xFF111A28)
private val DarkSurfaceContainer = Color(0xFF162333)
private val DarkSurfaceContainerHigh = Color(0xFF1B2939)
private val DarkSurfaceContainerHighest = Color(0xFF213040)

private val Success = Color(0xFF15803D)
private val SuccessDark = Color(0xFF4ADE80)

internal val SpellCoachLightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = BlueOnPrimary,
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001F2E),
    secondary = BlueSecondary,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFB6DDEF),
    onSecondaryContainer = Color(0xFF001F2E),
    tertiary = PurpleTertiary,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF3E8FF),
    onTertiaryContainer = Color(0xFF2E1065),
    background = LightBackground,
    onBackground = Color(0xFF1A1C1E),
    surface = LightSurface,
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = LightSurfaceVariant,
    surfaceDim = Color(0xFFD3DAE4),
    surfaceBright = Color(0xFFFFFFFF),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = LightSurfaceContainerLow,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerHigh = LightSurfaceContainerHigh,
    surfaceContainerHighest = LightSurfaceContainerHighest,
    onSurfaceVariant = Color(0xFF454952),
    outline = Color(0xFFB6DDEF),
    outlineVariant = Color(0xFFC5CED8),
    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFF1F2),
    onErrorContainer = Color(0xFF7F1D1D)
)

internal val SpellCoachDarkColorScheme = darkColorScheme(
    primary = BluePrimaryDark,
    onPrimary = BlueOnPrimaryDark,
    primaryContainer = Color(0xFF1E3A5F),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = BlueSecondaryDark,
    onSecondary = BlueOnPrimaryDark,
    secondaryContainer = Color(0xFF1A4A63),
    onSecondaryContainer = Color(0xFFE0F7FF),
    tertiary = PurpleTertiaryDark,
    onTertiary = Color(0xFF2E1065),
    tertiaryContainer = Color(0xFF3B255C),
    onTertiaryContainer = Color(0xFFF3E8FF),
    background = DarkBackground,
    onBackground = Color(0xFFE8EDF5),
    surface = DarkSurface,
    onSurface = Color(0xFFE8EDF5),
    surfaceVariant = DarkSurfaceVariant,
    surfaceDim = Color(0xFF060A10),
    surfaceBright = Color(0xFF3A4F66),
    surfaceContainerLowest = Color(0xFF060A10),
    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest,
    onSurfaceVariant = Color(0xFFB9C7D9),
    outline = Color(0xFF3D5A73),
    outlineVariant = Color(0xFF2A3F52),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    inverseSurface = Color(0xFFE8EDF5),
    inverseOnSurface = Color(0xFF1A1C1E),
    inversePrimary = BluePrimary
)

object SpellCoachSemantic {
    val successLight = Success
    val successDark = SuccessDark
}
