package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.BookmarkEntity
import com.example.data.local.TasbeehEntity
import com.example.data.prayer.CalculationMethod
import com.example.data.prayer.CityPreset
import com.example.data.prayer.DayPrayerSchedule
import com.example.data.prayer.PrayerAlertMode
import com.example.data.prayer.PrayerCalculator
import com.example.data.prayer.PrayerType
import com.example.data.prayer.SinglePrayer
import com.example.data.prayer.WORLD_CITIES
import com.example.data.quran.Ayah
import com.example.data.quran.Qari
import com.example.data.quran.QuranRepository
import com.example.data.quran.RECITER_LIST
import com.example.data.quran.Surah
import com.example.service.AthanNotificationHelper
import com.example.service.AudioPlaybackState
import com.example.service.AudioPlayerManager
import com.example.service.LocationHelper
import com.example.service.LocationResult
import com.example.service.MUEZZIN_LIST
import com.example.service.Muezzin
import com.example.service.QiblaSensorManager
import com.example.ui.localization.AppLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val bookmarkDao = db.bookmarkDao()
    private val tasbeehDao = db.tasbeehDao()

    val locationHelper = LocationHelper(application)
    val sensorManager = QiblaSensorManager(application)
    val audioPlayerManager = AudioPlayerManager(application)
    val athanNotificationHelper = AthanNotificationHelper(application)

    // Language & Theme State
    private val _currentLanguage = MutableStateFlow(AppLanguage.detectSystemLanguage())
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _isDarkTheme = MutableStateFlow<Boolean?>(null) // null = system
    val isDarkTheme: StateFlow<Boolean?> = _isDarkTheme.asStateFlow()

    private val _themePalette = MutableStateFlow(com.example.ui.theme.AppThemePalette.EMERALD)
    val themePalette: StateFlow<com.example.ui.theme.AppThemePalette> = _themePalette.asStateFlow()

    private val _customAppName = MutableStateFlow("نور الإيمان")
    val customAppName: StateFlow<String> = _customAppName.asStateFlow()

    // Location & Prayer State
    private val _currentLocation = MutableStateFlow<LocationResult>(
        LocationResult(
            cityName = "Makkah",
            countryName = "Saudi Arabia",
            latitude = 21.4225,
            longitude = 39.8262,
            isGps = false
        )
    )
    val currentLocation: StateFlow<LocationResult> = _currentLocation.asStateFlow()

    private val _isLocating = MutableStateFlow(false)
    val isLocating: StateFlow<Boolean> = _isLocating.asStateFlow()

    private val _calculationMethod = MutableStateFlow(CalculationMethod.UMM_AL_QURA)
    val calculationMethod: StateFlow<CalculationMethod> = _calculationMethod.asStateFlow()

    private val _prayerSchedule = MutableStateFlow<DayPrayerSchedule?>(null)
    val prayerSchedule: StateFlow<DayPrayerSchedule?> = _prayerSchedule.asStateFlow()

    private val _prayerAlertModes = MutableStateFlow(
        PrayerType.entries.associateWith { PrayerAlertMode.FULL_ATHAN }
    )
    val prayerAlertModes: StateFlow<Map<PrayerType, PrayerAlertMode>> = _prayerAlertModes.asStateFlow()

    private val _selectedMuezzin = MutableStateFlow(MUEZZIN_LIST[0])
    val selectedMuezzin: StateFlow<Muezzin> = _selectedMuezzin.asStateFlow()

    // Qibla Sensor
    val azimuthHeading: StateFlow<Float> = sensorManager.azimuthHeading
    val isSensorAvailable: StateFlow<Boolean> = sensorManager.isSensorAvailable

    // Quran State
    val surahs: List<Surah> = QuranRepository.surahs

    private val _selectedQari = MutableStateFlow(RECITER_LIST[0])
    val selectedQari: StateFlow<Qari> = _selectedQari.asStateFlow()

    private val _quranFontSize = MutableStateFlow(24f)
    val quranFontSize: StateFlow<Float> = _quranFontSize.asStateFlow()

    private val _activeSurah = MutableStateFlow(surahs[0])
    val activeSurah: StateFlow<Surah> = _activeSurah.asStateFlow()

    private val _activeAyahs = MutableStateFlow<List<Ayah>>(emptyList())
    val activeAyahs: StateFlow<List<Ayah>> = _activeAyahs.asStateFlow()

    private val _isLoadingAyahs = MutableStateFlow(false)
    val isLoadingAyahs: StateFlow<Boolean> = _isLoadingAyahs.asStateFlow()

    val audioPlaybackState: StateFlow<AudioPlaybackState> = audioPlayerManager.playbackState

    val bookmarks: StateFlow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val tasbeehCounters: StateFlow<List<TasbeehEntity>> = tasbeehDao.getAllTasbeeh()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private var countdownJob: Job? = null

    init {
        // Setup initial default location based on device country
        val defaultCity = locationHelper.getDefaultCity()
        _currentLocation.value = LocationResult(
            cityName = defaultCity.nameEn,
            countryName = defaultCity.countryEn,
            latitude = defaultCity.latitude,
            longitude = defaultCity.longitude,
            isGps = false
        )
        _calculationMethod.value = defaultCity.defaultMethod

        recalculatePrayers()
        startLiveCountdown()
        loadSurahAyahs(1)

        // Setup auto-next ayah playback
        audioPlayerManager.setOnAyahCompleted {
            playNextAyah()
        }

        // Try detecting GPS on startup in background
        detectGpsLocation()
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        recalculatePrayers()
    }

    fun setDarkTheme(dark: Boolean?) {
        _isDarkTheme.value = dark
    }

    fun setThemePalette(palette: com.example.ui.theme.AppThemePalette) {
        _themePalette.value = palette
    }

    fun setCustomAppName(name: String) {
        if (name.isNotBlank()) {
            _customAppName.value = name.trim()
        }
    }

    fun setCalculationMethod(method: CalculationMethod) {
        _calculationMethod.value = method
        recalculatePrayers()
    }

    fun setMuezzin(muezzin: Muezzin) {
        _selectedMuezzin.value = muezzin
    }

    fun setQari(qari: Qari) {
        _selectedQari.value = qari
    }

    fun setQuranFontSize(size: Float) {
        _quranFontSize.value = size
    }

    fun selectCity(city: CityPreset) {
        _currentLocation.value = LocationResult(
            cityName = city.nameEn,
            countryName = city.countryEn,
            latitude = city.latitude,
            longitude = city.longitude,
            isGps = false
        )
        _calculationMethod.value = city.defaultMethod
        recalculatePrayers()
    }

    fun detectGpsLocation() {
        viewModelScope.launch {
            _isLocating.value = true
            val gps = locationHelper.getCurrentGpsLocation()
            if (gps != null) {
                _currentLocation.value = gps
                recalculatePrayers()
            }
            _isLocating.value = false
        }
    }

    fun setPrayerAlertMode(prayer: PrayerType, mode: PrayerAlertMode) {
        val updated = _prayerAlertModes.value.toMutableMap()
        updated[prayer] = mode
        _prayerAlertModes.value = updated
    }

    fun recalculatePrayers() {
        val loc = _currentLocation.value
        val method = _calculationMethod.value
        val cal = Calendar.getInstance()
        val prayerDates = PrayerCalculator.calculatePrayerTimes(cal, loc.latitude, loc.longitude, method)

        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val now = System.currentTimeMillis()

        var nextPrayerFound: SinglePrayer? = null
        var minRemainingMs = Long.MAX_VALUE

        val singlePrayers = prayerDates.map { (type, date) ->
            val diff = date.time - now
            val isNext = if (diff > 0 && diff < minRemainingMs) {
                minRemainingMs = diff
                nextPrayerFound = SinglePrayer(type, timeFormat.format(date), date.time, isNext = true)
                true
            } else {
                false
            }
            SinglePrayer(
                type = type,
                timeFormatted = timeFormat.format(date),
                epochMillis = date.time,
                isNext = isNext
            )
        }

        // If all prayers today have passed, next prayer is tomorrow's Fajr
        if (nextPrayerFound == null && singlePrayers.isNotEmpty()) {
            val tomorrowCal = (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }
            val tomorrowPrayers = PrayerCalculator.calculatePrayerTimes(tomorrowCal, loc.latitude, loc.longitude, method)
            val tomorrowFajr = tomorrowPrayers[PrayerType.FAJR]
            if (tomorrowFajr != null) {
                nextPrayerFound = SinglePrayer(
                    PrayerType.FAJR,
                    timeFormat.format(tomorrowFajr),
                    tomorrowFajr.time,
                    isNext = true
                )
                minRemainingMs = tomorrowFajr.time - now
            }
        }

        val gregorianFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
        val isArabic = _currentLanguage.value == AppLanguage.ARABIC
        val hijriStr = PrayerCalculator.getEstimatedHijriDate(cal, isArabic)
        val qiblaBearing = PrayerCalculator.calculateQiblaBearing(loc.latitude, loc.longitude)
        val distanceKm = PrayerCalculator.calculateDistanceToKaabaKm(loc.latitude, loc.longitude)

        _prayerSchedule.value = DayPrayerSchedule(
            prayers = singlePrayers,
            nextPrayer = nextPrayerFound,
            remainingSeconds = (minRemainingMs / 1000).coerceAtLeast(0),
            hijriDate = hijriStr,
            gregorianDate = gregorianFormat.format(cal.time),
            locationName = if (isArabic) loc.cityName else "${loc.cityName}, ${loc.countryName}",
            qiblaBearing = qiblaBearing,
            distanceToMakkahKm = distanceKm
        )
    }

    private fun startLiveCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                val current = _prayerSchedule.value
                if (current != null && current.nextPrayer != null) {
                    val remaining = (current.nextPrayer.epochMillis - System.currentTimeMillis()) / 1000
                    if (remaining <= 0) {
                        recalculatePrayers()
                    } else {
                        _prayerSchedule.value = current.copy(remainingSeconds = remaining)
                    }
                }
            }
        }
    }

    fun loadSurahAyahs(surahNumber: Int) {
        val s = surahs.firstOrNull { it.number == surahNumber } ?: return
        _activeSurah.value = s
        viewModelScope.launch {
            _isLoadingAyahs.value = true
            val ayahs = QuranRepository.getAyahsForSurah(surahNumber)
            _activeAyahs.value = ayahs
            _isLoadingAyahs.value = false
        }
    }

    fun playAyahAudio(ayah: Ayah) {
        val qari = _selectedQari.value
        val url = QuranRepository.getAudioUrlForAyah(qari, ayah.surahNumber, ayah.numberInSurah)
        val title = "Surah ${_activeSurah.value.nameArabic} - Ayah ${ayah.numberInSurah}"
        audioPlayerManager.playAudioUrl(url, ayah.surahNumber, ayah.numberInSurah, qari.nameEnglish, title)
    }

    fun playFullSurah(surah: Surah) {
        val qari = _selectedQari.value
        val url = QuranRepository.getFullSurahAudioUrl(qari, surah.number)
        val title = "سورة ${surah.nameArabic} (كاملة)"
        audioPlayerManager.playAudioUrl(url, surah.number, 1, qari.nameEnglish, title)
    }

    fun playNextAyah() {
        val currentState = audioPlayerManager.playbackState.value
        val currentAyahNum = currentState.currentAyah
        val currentSurahNum = currentState.currentSurah
        val surah = surahs.firstOrNull { it.number == currentSurahNum } ?: return

        if (currentAyahNum < surah.numberOfAyahs) {
            val nextAyahNum = currentAyahNum + 1
            val nextAyah = _activeAyahs.value.firstOrNull { it.numberInSurah == nextAyahNum }
            if (nextAyah != null) {
                playAyahAudio(nextAyah)
            }
        }
    }

    fun playPreviousAyah() {
        val currentState = audioPlayerManager.playbackState.value
        val currentAyahNum = currentState.currentAyah
        if (currentAyahNum > 1) {
            val prevAyahNum = currentAyahNum - 1
            val prevAyah = _activeAyahs.value.firstOrNull { it.numberInSurah == prevAyahNum }
            if (prevAyah != null) {
                playAyahAudio(prevAyah)
            }
        }
    }

    fun toggleBookmark(ayah: Ayah, surahNameAr: String, surahNameEn: String) {
        viewModelScope.launch {
            val id = ayah.id
            val exists = bookmarks.value.any { it.id == id }
            if (exists) {
                bookmarkDao.deleteById(id)
            } else {
                bookmarkDao.insertBookmark(
                    BookmarkEntity(
                        id = id,
                        surahNumber = ayah.surahNumber,
                        ayahNumber = ayah.numberInSurah,
                        surahNameArabic = surahNameAr,
                        surahNameEnglish = surahNameEn,
                        ayahText = ayah.textArabic
                    )
                )
            }
        }
    }

    fun incrementTasbeeh(id: String, arabicText: String, translation: String, target: Int) {
        viewModelScope.launch {
            val existing = tasbeehDao.getById(id)
            if (existing != null) {
                val newCount = existing.count + 1
                val completedRounds = if (newCount >= existing.target) existing.totalDone + 1 else existing.totalDone
                val resetCount = if (newCount >= existing.target) 0 else newCount

                tasbeehDao.insertOrUpdate(
                    existing.copy(
                        count = resetCount,
                        totalDone = completedRounds,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            } else {
                tasbeehDao.insertOrUpdate(
                    TasbeehEntity(
                        id = id,
                        arabicText = arabicText,
                        translation = translation,
                        count = 1,
                        target = target,
                        totalDone = 0
                    )
                )
            }
        }
    }

    fun resetTasbeeh(id: String) {
        viewModelScope.launch {
            val existing = tasbeehDao.getById(id)
            if (existing != null) {
                tasbeehDao.insertOrUpdate(existing.copy(count = 0))
            }
        }
    }

    fun startQiblaSensor() {
        sensorManager.startListening()
    }

    fun stopQiblaSensor() {
        sensorManager.stopListening()
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
        sensorManager.stopListening()
        audioPlayerManager.release()
        athanNotificationHelper.stopAthan()
    }
}
