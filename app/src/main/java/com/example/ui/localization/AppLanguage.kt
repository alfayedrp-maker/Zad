package com.example.ui.localization

import androidx.compose.ui.unit.LayoutDirection
import java.util.Locale

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val layoutDirection: LayoutDirection
) {
    ARABIC("ar", "Arabic", "العربية", LayoutDirection.Rtl),
    ENGLISH("en", "English", "English", LayoutDirection.Ltr),
    FRENCH("fr", "French", "Français", LayoutDirection.Ltr),
    SPANISH("es", "Spanish", "Español", LayoutDirection.Ltr),
    TURKISH("tr", "Turkish", "Türkçe", LayoutDirection.Ltr),
    INDONESIAN("id", "Indonesian", "Bahasa Indonesia", LayoutDirection.Ltr),
    URDU("ur", "Urdu", "اردو", LayoutDirection.Rtl),
    PORTUGUESE("pt", "Portuguese", "Português", LayoutDirection.Ltr),
    ITALIAN("it", "Italian", "Italiano", LayoutDirection.Ltr);

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) }
                ?: ENGLISH
        }

        fun detectSystemLanguage(): AppLanguage {
            val systemCode = Locale.getDefault().language.lowercase()
            return entries.firstOrNull { it.code == systemCode } ?: ENGLISH
        }
    }
}
