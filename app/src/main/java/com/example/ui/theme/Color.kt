package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Default Emerald Islamic Palette
val EmeraldPrimary = Color(0xFF0D5C3A)
val EmeraldDark = Color(0xFF083C25)
val EmeraldLight = Color(0xFF1B8A5A)
val GoldAccent = Color(0xFFD4AF37)
val GoldLight = Color(0xFFFFDF73)
val WarmSand = Color(0xFFF4EDE2)
val SoftBackgroundLight = Color(0xFFF7FAF7)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFEAEFEA)
val TextPrimaryLight = Color(0xFF14241C)
val TextSecondaryLight = Color(0xFF5A7265)

// Dark Theme Colors
val EmeraldPrimaryDark = Color(0xFF28A870)
val DarkBackground = Color(0xFF07140E)
val DarkSurface = Color(0xFF0F241B)
val DarkSurfaceVariant = Color(0xFF163327)
val DarkCard = Color(0xFF142C21)
val TextPrimaryDark = Color(0xFFEFF7F2)
val TextSecondaryDark = Color(0xFFA1BCAD)
val GoldDarkTheme = Color(0xFFFFD54F)

// Prayer Highlight Colors
val FajrColor = Color(0xFF4A6984)
val ShurooqColor = Color(0xFFE59866)
val DhuhrColor = Color(0xFFE5B834)
val AsrColor = Color(0xFFDF7A32)
val MaghribColor = Color(0xFFC0392B)
val IshaColor = Color(0xFF2C3E50)

/**
 * Multiple Theme Palettes for user customization
 */
enum class AppThemePalette(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val primaryLight: Color,
    val primaryDark: Color,
    val containerLight: Color,
    val containerDark: Color,
    val accentColor: Color,
    val previewGradient: List<Color>
) {
    EMERALD(
        id = "emerald",
        nameAr = "الزمردي الإسلامي",
        nameEn = "Emerald Green",
        primaryLight = Color(0xFF0D5C3A),
        primaryDark = Color(0xFF28A870),
        containerLight = Color(0xFFD4EFE2),
        containerDark = Color(0xFF163327),
        accentColor = Color(0xFFD4AF37),
        previewGradient = listOf(Color(0xFF0D5C3A), Color(0xFF1B8A5A), Color(0xFFD4AF37))
    ),
    SAPPHIRE(
        id = "sapphire",
        nameAr = "الأزرق الياقوتي",
        nameEn = "Royal Sapphire",
        primaryLight = Color(0xFF155084),
        primaryDark = Color(0xFF4B94DE),
        containerLight = Color(0xFFD9E7F6),
        containerDark = Color(0xFF152A42),
        accentColor = Color(0xFFE5B834),
        previewGradient = listOf(Color(0xFF155084), Color(0xFF2E7BBF), Color(0xFFE5B834))
    ),
    AMBER(
        id = "amber",
        nameAr = "العنبر الأندلسي",
        nameEn = "Andalusian Amber",
        primaryLight = Color(0xFF8B4D08),
        primaryDark = Color(0xFFE59838),
        containerLight = Color(0xFFFBEAD2),
        containerDark = Color(0xFF38230D),
        accentColor = Color(0xFF0D5C3A),
        previewGradient = listOf(Color(0xFF8B4D08), Color(0xFFC97B1A), Color(0xFF0D5C3A))
    ),
    TEAL(
        id = "teal",
        nameAr = "الفيروزي الهادئ",
        nameEn = "Deep Turquoise",
        primaryLight = Color(0xFF0B6669),
        primaryDark = Color(0xFF32A89C),
        containerLight = Color(0xFFD2F2F2),
        containerDark = Color(0xFF113233),
        accentColor = Color(0xFFD4AF37),
        previewGradient = listOf(Color(0xFF0B6669), Color(0xFF179296), Color(0xFFD4AF37))
    ),
    PURPLE(
        id = "purple",
        nameAr = "البنفسجي الملكي",
        nameEn = "Royal Violet",
        primaryLight = Color(0xFF5E2B82),
        primaryDark = Color(0xFFA664D6),
        containerLight = Color(0xFFEEDBFA),
        containerDark = Color(0xFF2E1542),
        accentColor = Color(0xFFFFD54F),
        previewGradient = listOf(Color(0xFF5E2B82), Color(0xFF8540B5), Color(0xFFFFD54F))
    ),
    MIDNIGHT_SLATE(
        id = "midnight_slate",
        nameAr = "الرمادي الفحمي",
        nameEn = "Midnight Slate",
        primaryLight = Color(0xFF2C3E50),
        primaryDark = Color(0xFF7F8C8D),
        containerLight = Color(0xFFDFE6E9),
        containerDark = Color(0xFF1E272E),
        accentColor = Color(0xFFD4AF37),
        previewGradient = listOf(Color(0xFF2C3E50), Color(0xFF34495E), Color(0xFFD4AF37))
    )
}
