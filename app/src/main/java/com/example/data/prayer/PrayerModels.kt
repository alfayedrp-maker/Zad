package com.example.data.prayer

enum class PrayerType(val key: String) {
    FAJR("fajr"),
    SUNRISE("sunrise"),
    DHUHR("dhuhr"),
    ASR("asr"),
    MAGHRIB("maghrib"),
    ISHA("isha")
}

enum class CalculationMethod(val id: Int, val title: String) {
    UMM_AL_QURA(4, "Umm Al-Qura (Makkah) - جامعة أم القرى"),
    EGYPTIAN(5, "Egyptian General Authority - الهيئة المصرية العامة"),
    MUSLIM_WORLD_LEAGUE(3, "Muslim World League - رابطة العالم الإسلامي"),
    ISNA(2, "ISNA (North America) - أمريكا الشمالية"),
    KARACHI(1, "Univ. of Islamic Sciences, Karachi - كراتشي"),
    GULF(8, "Gulf Region (UAE / Kuwait / Qatar) - الخليج"),
    TURKEY(13, "Diyanet (Turkey) - رئاسة الشؤون الدينية تركيا")
}

enum class PrayerAlertMode {
    FULL_ATHAN,
    TAKBEER_ONLY,
    GENTLE_TONE,
    SILENT
}

data class SinglePrayer(
    val type: PrayerType,
    val timeFormatted: String,
    val epochMillis: Long,
    val isNext: Boolean = false,
    val isCurrent: Boolean = false
)

data class DayPrayerSchedule(
    val prayers: List<SinglePrayer>,
    val nextPrayer: SinglePrayer?,
    val remainingSeconds: Long,
    val hijriDate: String,
    val gregorianDate: String,
    val locationName: String,
    val qiblaBearing: Double,
    val distanceToMakkahKm: Int
)

data class CityPreset(
    val nameEn: String,
    val nameAr: String,
    val countryEn: String,
    val countryAr: String,
    val latitude: Double,
    val longitude: Double,
    val defaultMethod: CalculationMethod = CalculationMethod.UMM_AL_QURA
)

val WORLD_CITIES = listOf(
    CityPreset("Makkah", "مكة المكرمة", "Saudi Arabia", "المملكة العربية السعودية", 21.4225, 39.8262, CalculationMethod.UMM_AL_QURA),
    CityPreset("Madinah", "المدينة المنورة", "Saudi Arabia", "المملكة العربية السعودية", 24.4672, 39.6111, CalculationMethod.UMM_AL_QURA),
    CityPreset("Riyadh", "الرياض", "Saudi Arabia", "المملكة العربية السعودية", 24.7136, 46.6753, CalculationMethod.UMM_AL_QURA),
    CityPreset("Cairo", "القاهرة", "Egypt", "مصر", 30.0444, 31.2357, CalculationMethod.EGYPTIAN),
    CityPreset("Alexandria", "الإسكندرية", "Egypt", "مصر", 31.2001, 29.9187, CalculationMethod.EGYPTIAN),
    CityPreset("Jerusalem (Al-Quds)", "القدس الشريف", "Palestine", "فلسطين", 31.7683, 35.2137, CalculationMethod.MUSLIM_WORLD_LEAGUE),
    CityPreset("Dubai", "دبي", "United Arab Emirates", "الإمارات العربية المتحدة", 25.2048, 55.2708, CalculationMethod.GULF),
    CityPreset("Abu Dhabi", "أبوظبي", "United Arab Emirates", "الإمارات العربية المتحدة", 24.4539, 54.3773, CalculationMethod.GULF),
    CityPreset("Doha", "الدوحة", "Qatar", "قطر", 25.2854, 51.5310, CalculationMethod.GULF),
    CityPreset("Kuwait City", "الكويت", "Kuwait", "الكويت", 29.3759, 47.9774, CalculationMethod.GULF),
    CityPreset("Istanbul", "إسطنبول", "Turkey", "تركيا", 41.0082, 28.9784, CalculationMethod.TURKEY),
    CityPreset("Ankara", "أنقرة", "Turkey", "تركيا", 39.9334, 32.8597, CalculationMethod.TURKEY),
    CityPreset("Jakarta", "جاكرتا", "Indonesia", "إندونيسيا", -6.2088, 106.8456, CalculationMethod.MUSLIM_WORLD_LEAGUE),
    CityPreset("Kuala Lumpur", "كوالالمبور", "Malaysia", "ماليزيا", 3.1390, 101.6869, CalculationMethod.MUSLIM_WORLD_LEAGUE),
    CityPreset("Karachi", "كراتشي", "Pakistan", "باكستان", 24.8607, 67.0011, CalculationMethod.KARACHI),
    CityPreset("Islamabad", "إسلام آباد", "Pakistan", "باكستان", 33.6844, 73.0479, CalculationMethod.KARACHI),
    CityPreset("London", "لندن", "United Kingdom", "المملكة المتحدة", 51.5074, -0.1278, CalculationMethod.MUSLIM_WORLD_LEAGUE),
    CityPreset("Paris", "باريس", "France", "فرنسا", 48.8566, 2.3522, CalculationMethod.MUSLIM_WORLD_LEAGUE),
    CityPreset("New York", "نيويورك", "United States", "الولايات المتحدة", 40.7128, -74.0060, CalculationMethod.ISNA),
    CityPreset("Chicago", "شيكاغو", "United States", "الولايات المتحدة", 41.8781, -87.6298, CalculationMethod.ISNA),
    CityPreset("Toronto", "تورونتو", "Canada", "كندا", 43.6532, -79.3832, CalculationMethod.ISNA),
    CityPreset("Madrid", "مدريد", "Spain", "إسبانيا", 40.4168, -3.7038, CalculationMethod.MUSLIM_WORLD_LEAGUE),
    CityPreset("Rome", "روما", "Italy", "إيطاليا", 41.9028, 12.4964, CalculationMethod.MUSLIM_WORLD_LEAGUE),
    CityPreset("Berlin", "برلين", "Germany", "ألمانيا", 52.5200, 13.4050, CalculationMethod.MUSLIM_WORLD_LEAGUE),
    CityPreset("Lisbon", "لشبونة", "Portugal", "البرتغال", 38.7223, -9.1393, CalculationMethod.MUSLIM_WORLD_LEAGUE)
)
