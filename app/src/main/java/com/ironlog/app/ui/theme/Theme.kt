package com.ironlog.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = IronRed,
    onPrimary = IronOnRed,
    primaryContainer = IronRedContainer,
    onPrimaryContainer = IronOnRedContainer,
    secondary = IronOnSurfaceVariant,
    onSecondary = Color.White,
    background = IronBackground,
    onBackground = IronOnBackground,
    surface = IronSurface,
    onSurface = IronOnSurface,
    surfaceVariant = IronSurfaceVariant,
    onSurfaceVariant = IronOnSurfaceVariant,
    outline = IronOutline,
    surfaceContainer = IronSurfaceVariant,
    surfaceContainerLow = IronBackground,
    surfaceContainerHigh = IronSurfaceVariant,
)

private val DarkColors = darkColorScheme(
    primary = IronRedDark,
    onPrimary = IronOnRedDark,
    primaryContainer = IronRedContainerDark,
    onPrimaryContainer = IronOnRedContainerDark,
    secondary = IronOnSurfaceVariantDark,
    onSecondary = Color(0xFF3B2F2F),
    background = IronBackgroundDark,
    onBackground = IronOnBackgroundDark,
    surface = IronSurfaceDark,
    onSurface = IronOnSurfaceDark,
    surfaceVariant = IronSurfaceVariantDark,
    onSurfaceVariant = IronOnSurfaceVariantDark,
    outline = IronOutlineDark,
    surfaceContainer = IronSurfaceDark,
    surfaceContainerLow = IronBackgroundDark,
    surfaceContainerHigh = IronSurfaceVariantDark,
)

enum class ThemeMode { SYSTEM, LIGHT, DARK }

/** @param darkTheme resolved by the caller (settings "theme_mode" > system). */
@Composable
fun IronLogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = IronLogTypography,
        content = content,
    )
}
