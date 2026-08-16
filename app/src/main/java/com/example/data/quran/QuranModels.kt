package com.example.data.quran

data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val englishTranslation: String,
    val numberOfAyahs: Int,
    val revelationType: RevelationType,
    val juzNumber: Int
)

enum class RevelationType {
    MECCAN,
    MEDINAN
}

data class Ayah(
    val surahNumber: Int,
    val numberInSurah: Int,
    val textArabic: String,
    val translationEnglish: String = "",
    val translationArabic: String = ""
) {
    val id: String get() = "$surahNumber:$numberInSurah"
}

data class Qari(
    val id: String,
    val nameArabic: String,
    val nameEnglish: String,
    val subfolder: String,
    val surahBaseUrl: String
)

val RECITER_LIST = listOf(
    Qari(
        id = "alafasy",
        nameArabic = "مشاري راشد العفاسي",
        nameEnglish = "Mishary Rashid Alafasy",
        subfolder = "Alafasy_128kbps",
        surahBaseUrl = "https://server8.mp3quran.net/afs/"
    ),
    Qari(
        id = "abdulbasit",
        nameArabic = "عبد الباسط عبد الصمد",
        nameEnglish = "Abdulbasit Abdussamad (Murattal)",
        subfolder = "Abdul_Basit_Murattal_192kbps",
        surahBaseUrl = "https://server7.mp3quran.net/basit/"
    ),
    Qari(
        id = "hussary",
        nameArabic = "محمود خليل الحصري",
        nameEnglish = "Mahmoud Khalil Al-Hussary",
        subfolder = "Husary_128kbps",
        surahBaseUrl = "https://server13.mp3quran.net/husr/"
    ),
    Qari(
        id = "ghamdi",
        nameArabic = "سعد الغامدي",
        nameEnglish = "Saad Al-Ghamdi",
        subfolder = "Ghamadi_40kbps",
        surahBaseUrl = "https://server7.mp3quran.net/s_gmd/"
    ),
    Qari(
        id = "muaiqly",
        nameArabic = "ماهر المعيقلي",
        nameEnglish = "Maher Al-Muaiqly",
        subfolder = "Maher_AlMuaiqly_64kbps",
        surahBaseUrl = "https://server12.mp3quran.net/maher/"
    )
)
