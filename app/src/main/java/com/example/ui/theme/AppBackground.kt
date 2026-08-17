package com.example.ui.theme

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Composable
fun AppDynamicBackground(
    backgroundStyle: AppBackgroundStyle,
    palette: AppThemePalette,
    isDark: Boolean = isSystemInDarkTheme(),
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Crossfade(
            targetState = backgroundStyle,
            animationSpec = tween(500),
            label = "bg_crossfade"
        ) { style ->
            Box(modifier = Modifier.fillMaxSize()) {
                if (style.drawableResId != null) {
                    Image(
                        painter = painterResource(id = style.drawableResId),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Theme-tinted gradient overlay for high legibility
                    val overlayColor = if (isDark) {
                        Color(0xFF07140E)
                    } else {
                        Color(0xFFF7FAF7)
                    }
                    val primaryTint = if (isDark) palette.primaryDark else palette.primaryLight
                    val overlayAlpha = if (isDark) style.overlayAlphaDark else style.overlayAlphaLight

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        overlayColor.copy(alpha = overlayAlpha),
                                        overlayColor.copy(alpha = (overlayAlpha + 0.08f).coerceAtMost(0.96f)),
                                        primaryTint.copy(alpha = if (isDark) 0.25f else 0.12f),
                                        overlayColor.copy(alpha = (overlayAlpha + 0.05f).coerceAtMost(0.98f))
                                    )
                                )
                            )
                    )
                } else if (style == AppBackgroundStyle.SPIRITUAL_AURA) {
                    val primaryColor = if (isDark) palette.primaryDark else palette.primaryLight
                    val baseColor = if (isDark) Color(0xFF08150F) else Color(0xFFF5F9F6)

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        primaryColor.copy(alpha = if (isDark) 0.30f else 0.15f),
                                        GoldAccent.copy(alpha = if (isDark) 0.15f else 0.08f),
                                        baseColor
                                    ),
                                    radius = 1200f
                                )
                            )
                    )
                } else {
                    // Minimal Classic
                    val baseColor = if (isDark) Color(0xFF091710) else Color(0xFFF7FAF7)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(baseColor)
                    )
                }
            }
        }

        // Main app screen content
        content()
    }
}
