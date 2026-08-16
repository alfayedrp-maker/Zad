package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun NoorAlImanTheme(
    palette: AppThemePalette = AppThemePalette.EMERALD,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = palette.primaryDark,
            onPrimary = Color.Black,
            primaryContainer = palette.containerDark,
            onPrimaryContainer = Color.White,
            secondary = palette.accentColor,
            onSecondary = Color.Black,
            secondaryContainer = Color(0xFF332B14),
            onSecondaryContainer = GoldLight,
            tertiary = palette.accentColor,
            background = DarkBackground,
            onBackground = TextPrimaryDark,
            surface = DarkSurface,
            onSurface = TextPrimaryDark,
            surfaceVariant = DarkSurfaceVariant,
            onSurfaceVariant = TextSecondaryDark,
            outline = palette.primaryDark.copy(alpha = 0.4f)
        )
    } else {
        lightColorScheme(
            primary = palette.primaryLight,
            onPrimary = Color.White,
            primaryContainer = palette.containerLight,
            onPrimaryContainer = palette.primaryLight,
            secondary = palette.accentColor,
            onSecondary = Color.Black,
            secondaryContainer = WarmSand,
            onSecondaryContainer = Color(0xFF4A3B10),
            tertiary = palette.accentColor,
            background = SoftBackgroundLight,
            onBackground = TextPrimaryLight,
            surface = SurfaceLight,
            onSurface = TextPrimaryLight,
            surfaceVariant = SurfaceVariantLight,
            onSurfaceVariant = TextSecondaryLight,
            outline = Color(0xFFD0DCD5)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
