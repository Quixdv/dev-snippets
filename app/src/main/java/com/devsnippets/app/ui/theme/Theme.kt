package com.devsnippets.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.devsnippets.app.data.local.AppTheme

private fun darkSchemeFor(primary: Color, secondary: Color, tertiary: Color) = darkColorScheme(
    primary = primary,
    onPrimary = Color.White,
    secondary = secondary,
    onSecondary = Color.Black,
    tertiary = tertiary,
    background = DarkBackground,
    onBackground = Color(0xFFEDEDF2),
    surface = DarkSurface,
    onSurface = Color(0xFFEDEDF2),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFB8B8C6),
    outline = DarkOutline,
    error = ErrorRed,
    onError = Color.White
)

private fun lightSchemeFor(primary: Color, secondary: Color, tertiary: Color) = lightColorScheme(
    primary = primary,
    onPrimary = Color.White,
    secondary = secondary,
    onSecondary = Color.Black,
    tertiary = tertiary,
    background = LightBackground,
    onBackground = Color(0xFF17171F),
    surface = LightSurface,
    onSurface = Color(0xFF17171F),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF5B5B6B),
    outline = LightOutline,
    error = ErrorRed,
    onError = Color.White
)

/**
 * Resolves the Material3 color scheme for a given [AppTheme] preset and
 * dark/light mode. Called from the root Composable based on user settings.
 */
@Composable
fun DevSnippetsTheme(
    darkTheme: Boolean = true,
    appTheme: AppTheme = AppTheme.PURPLE_NEON,
    content: @Composable () -> Unit
) {
    val (primary, secondary, tertiary) = when (appTheme) {
        AppTheme.PURPLE_NEON -> Triple(PurplePrimary, PurpleSecondary, PurpleTertiary)
        AppTheme.OCEAN_BLUE -> Triple(OceanPrimary, OceanSecondary, OceanTertiary)
        AppTheme.FOREST_GREEN -> Triple(ForestPrimary, ForestSecondary, ForestTertiary)
        AppTheme.SUNSET_ORANGE -> Triple(SunsetPrimary, SunsetSecondary, SunsetTertiary)
    }

    val colorScheme = if (darkTheme) {
        darkSchemeFor(primary, secondary, tertiary)
    } else {
        lightSchemeFor(primary, secondary, tertiary)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
