package com.example.data.prayer

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.*

object PrayerCalculator {

    const val KAABA_LAT = 21.422487
    const val KAABA_LON = 39.826206

    // Great Circle Qibla Bearing calculation
    fun calculateQiblaBearing(lat: Double, lon: Double): Double {
        val userLatRad = Math.toRadians(lat)
        val userLonRad = Math.toRadians(lon)
        val kaabaLatRad = Math.toRadians(KAABA_LAT)
        val kaabaLonRad = Math.toRadians(KAABA_LON)

        val deltaLon = kaabaLonRad - userLonRad

        val y = sin(deltaLon)
        val x = cos(userLatRad) * tan(kaabaLatRad) - sin(userLatRad) * cos(deltaLon)

        var bearing = Math.toDegrees(atan2(y, x))
        if (bearing < 0) {
            bearing += 360.0
        }
        return (bearing * 10).roundToInt() / 10.0
    }

    // Distance to Makkah in Kilometers
    fun calculateDistanceToKaabaKm(lat: Double, lon: Double): Int {
        val r = 6371.0 // Earth radius km
        val dLat = Math.toRadians(KAABA_LAT - lat)
        val dLon = Math.toRadians(KAABA_LON - lon)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat)) * cos(Math.toRadians(KAABA_LAT)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (r * c).roundToInt()
    }

    /**
     * High-precision offline solar equation calculator for prayer times
     */
    fun calculatePrayerTimes(
        calendar: Calendar,
        latitude: Double,
        longitude: Double,
        method: CalculationMethod = CalculationMethod.UMM_AL_QURA
    ): Map<PrayerType, Date> {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Julian Day calculation
        var a = year / 100
        var b = 2 - a + a / 4
        if (year < 1583) b = 0
        val julianDay = (365.25 * (year + 4716)).toInt() +
                (30.6001 * (month + 1)).toInt() +
                day + b - 1524.5

        val d = julianDay - 2451545.0 // Days since J2000.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sin(Math.toRadians(g)) + 0.020 * sin(Math.toRadians(2 * g)))
        val e = 23.439 - 0.00000036 * d
        val ra = fixAngle(Math.toDegrees(atan2(cos(Math.toRadians(e)) * sin(Math.toRadians(l)), cos(Math.toRadians(l))))) / 15.0

        val declination = Math.toDegrees(asin(sin(Math.toRadians(e)) * sin(Math.toRadians(l))))
        val eqTime = q / 15.0 - fixHour(ra)

        val timezoneOffset = calendar.timeZone.getOffset(calendar.timeInMillis) / 3600000.0

        // Solar Noon (Dhuhr base)
        val noon = fixHour(12.0 + timezoneOffset - (longitude / 15.0) - eqTime)

        // Twilight parameters by calculation method
        val (fajrAngle, ishaAngle, ishaMinutesFixed) = when (method) {
            CalculationMethod.UMM_AL_QURA -> Triple(18.5, 0.0, 90.0) // Umm Al Qura: Fajr 18.5 deg, Isha = Maghrib + 90 min
            CalculationMethod.EGYPTIAN -> Triple(19.5, 17.5, 0.0)
            CalculationMethod.MUSLIM_WORLD_LEAGUE -> Triple(18.0, 17.0, 0.0)
            CalculationMethod.ISNA -> Triple(15.0, 15.0, 0.0)
            CalculationMethod.KARACHI -> Triple(18.0, 18.0, 0.0)
            CalculationMethod.GULF -> Triple(19.5, 0.0, 90.0)
            CalculationMethod.TURKEY -> Triple(18.0, 17.0, 0.0)
        }

        // Sunrise & Sunset angle is 0.833 degrees
        val sunriseHourAngle = calculateHourAngle(-0.833, latitude, declination)
        val sunrise = noon - sunriseHourAngle
        val sunset = noon + sunriseHourAngle

        // Fajr
        val fajrHourAngle = calculateHourAngle(-fajrAngle, latitude, declination)
        val fajr = noon - fajrHourAngle

        // Asr (Shafi'i: shadow factor = 1)
        val asrAngle = -Math.toDegrees(atan(1.0 + tan(Math.toRadians(abs(latitude - declination)))))
        val asrHourAngle = calculateHourAngle(asrAngle, latitude, declination)
        val asr = noon + asrHourAngle

        // Maghrib = Sunset
        val maghrib = sunset

        // Isha
        val isha = if (ishaMinutesFixed > 0) {
            maghrib + (ishaMinutesFixed / 60.0)
        } else {
            val ishaHourAngle = calculateHourAngle(-ishaAngle, latitude, declination)
            noon + ishaHourAngle
        }

        return mapOf(
            PrayerType.FAJR to decimalToDate(calendar, fajr),
            PrayerType.SUNRISE to decimalToDate(calendar, sunrise),
            PrayerType.DHUHR to decimalToDate(calendar, noon),
            PrayerType.ASR to decimalToDate(calendar, asr),
            PrayerType.MAGHRIB to decimalToDate(calendar, maghrib),
            PrayerType.ISHA to decimalToDate(calendar, isha)
        )
    }

    private fun calculateHourAngle(angle: Double, latitude: Double, declination: Double): Double {
        val latRad = Math.toRadians(latitude)
        val decRad = Math.toRadians(declination)
        val angRad = Math.toRadians(angle)

        val cosVal = (sin(angRad) - sin(latRad) * sin(decRad)) / (cos(latRad) * cos(decRad))
        val clamped = cosVal.coerceIn(-1.0, 1.0)
        return Math.toDegrees(acos(clamped)) / 15.0
    }

    private fun fixAngle(a: Double): Double {
        var angle = a - 360.0 * (floor(a / 360.0))
        if (angle < 0) angle += 360.0
        return angle
    }

    private fun fixHour(h: Double): Double {
        var hour = h - 24.0 * (floor(h / 24.0))
        if (hour < 0) hour += 24.0
        return hour
    }

    private fun decimalToDate(baseCalendar: Calendar, decimalHours: Double): Date {
        val totalMinutes = (decimalHours * 60).roundToInt()
        val hours = (totalMinutes / 60) % 24
        val minutes = totalMinutes % 60

        val cal = baseCalendar.clone() as Calendar
        cal.set(Calendar.HOUR_OF_DAY, hours)
        cal.set(Calendar.MINUTE, minutes)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    // Hijri date string conversion algorithm
    fun getEstimatedHijriDate(calendar: Calendar, localeIsArabic: Boolean = false): String {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Umm Al Qura estimated calculation
        val m = month
        val y = year
        val jd = (1461 * (y + 4800 + (m - 14) / 12)) / 4 +
                (367 * (m - 2 - 12 * ((m - 14) / 12))) / 12 -
                (3 * ((y + 4900 + (m - 14) / 12) / 100)) / 4 +
                day - 32075

        val l = jd - 1948440 + 10632
        val n = (l - 1) / 10631
        val lPrime = l - 10631 * n + 354
        val j = ((10985 - lPrime) / 5316) * ((50 * lPrime) / 17719) +
                (lPrime / 5670) * ((43 * lPrime) / 15238)
        val lDoublePrime = lPrime - ((30 - j) / 15) * ((17719 * j) / 50) -
                (j / 16) * ((15238 * j) / 43) + 29
        val hijriMonth = (24 * lDoublePrime) / 709
        val hijriDay = lDoublePrime - (709 * hijriMonth) / 24
        val hijriYear = 30 * n + j - 30

        val arabicMonths = listOf(
            "محرم", "صفر", "ربيع الأول", "ربيع الثاني", "جمادى الأولى", "جمادى الآخرة",
            "رجب", "شعبان", "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
        )
        val englishMonths = listOf(
            "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani", "Jumada al-Awwal", "Jumada al-Thani",
            "Rajab", "Sha'ban", "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
        )

        val safeMonthIndex = (hijriMonth - 1).coerceIn(0, 11)
        val monthName = if (localeIsArabic) arabicMonths[safeMonthIndex] else englishMonths[safeMonthIndex]
        val suffix = if (localeIsArabic) "هـ" else "AH"

        return "$hijriDay $monthName $hijriYear $suffix"
    }
}
