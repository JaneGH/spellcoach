package com.example.spellcoach.core.designsystem.theme

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

private val LightBackground = Color(0xFFF8FAFC)
private val LightSurface = Color(0xFFFFFFFF)
private val LightSurfaceVariant = Color(0xFFE2E8F0)

private val DarkBackground = Color(0xFF0B1420)
private val DarkSurface = Color(0xFF152535)
private val DarkSurfaceVariant = Color(0xFF243447)

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
    onSurfaceVariant = Color(0xFF44474E),
    outline = Color(0xFFB6DDEF),
    outlineVariant = Color(0xFFCBD5E1),
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
    onSurfaceVariant = Color(0xFFB8C4D4),
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
