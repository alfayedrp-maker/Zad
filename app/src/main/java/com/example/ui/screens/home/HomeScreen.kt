package com.example.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.prayer.*
import com.example.ui.localization.LocalAppStrings
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToQuranSurah: (Int) -> Unit,
    onNavigateToQibla: () -> Unit,
    onNavigateToTasbeeh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val customAppName by viewModel.customAppName.collectAsState()
    val schedule by viewModel.prayerSchedule.collectAsState()
    val alertModes by viewModel.prayerAlertModes.collectAsState()
    val isLocating by viewModel.isLocating.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val backgroundStyle by viewModel.backgroundStyle.collectAsState()
    val themePalette by viewModel.themePalette.collectAsState()
    val selected3dSceneIndex by viewModel.selected3dSceneIndex.collectAsState()

    var showCityDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Islamic App Header with Emblem Logo
        item {
            IslamicAppHeader(appName = customAppName)
        }

        // 3D Islamic Showcase Card with high-res 3D Islamic Scenes
        item {
            Islamic3dShowcaseCard(
                selectedSceneIndex = selected3dSceneIndex,
                onSelectScene = { viewModel.setSelected3dSceneIndex(it) },
                onNavigateToQuran = { onNavigateToQuranSurah(1) },
                onNavigateToQibla = onNavigateToQibla,
                onNavigateToTasbeeh = onNavigateToTasbeeh
            )
        }

        // Background & Theme Color Quick Switcher Bar
        item {
            BackgroundAndThemeQuickBar(
                currentBackground = backgroundStyle,
                currentPalette = themePalette,
                onSelectBackground = { viewModel.setBackgroundStyle(it) },
                onSelectPalette = { viewModel.setThemePalette(it) }
            )
        }

        // Top Location and GPS bar
        item {
            LocationBar(
                cityName = currentLocation.cityName,
                countryName = currentLocation.countryName,
                isGps = currentLocation.isGps,
                isLocating = isLocating,
                onDetectGps = { viewModel.detectGpsLocation() },
                onChangeCity = { showCityDialog = true }
            )
        }

        // Hero Countdown Banner with Mosque Background Art
        item {
            schedule?.let { s ->
                HeroPrayerCountdownCard(
                    schedule = s,
                    onTestAthan = {
                        viewModel.athanNotificationHelper.playAthanPreview(viewModel.selectedMuezzin.value)
                    }
                )
            }
        }

        // Quick Islamic Actions / Shortcuts
        item {
            QuickShortcutsRow(
                onOpenKahf = { onNavigateToQuranSurah(18) },
                onOpenMulk = { onNavigateToQuranSurah(67) },
                onOpenYasin = { onNavigateToQuranSurah(36) },
                onOpenQibla = onNavigateToQibla,
                onOpenTasbeeh = onNavigateToTasbeeh
            )
        }

        // Today's Prayer Schedule Header
        item {
            Text(
                text = strings.todayPrayers,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }

        // Prayer Cards List
        schedule?.let { s ->
            items(s.prayers) { prayer ->
                PrayerCardItem(
                    prayer = prayer,
                    alertMode = alertModes[prayer.type] ?: PrayerAlertMode.FULL_ATHAN,
                    onToggleAlertMode = { currentMode ->
                        val nextMode = when (currentMode) {
                            PrayerAlertMode.FULL_ATHAN -> PrayerAlertMode.TAKBEER_ONLY
                            PrayerAlertMode.TAKBEER_ONLY -> PrayerAlertMode.GENTLE_TONE
                            PrayerAlertMode.GENTLE_TONE -> PrayerAlertMode.SILENT
                            PrayerAlertMode.SILENT -> PrayerAlertMode.FULL_ATHAN
                        }
                        viewModel.setPrayerAlertMode(prayer.type, nextMode)
                    }
                )
            }
        }

        // Daily Hadith / Reflection Card
        item {
            DailyInspirationCard()
        }
    }

    if (showCityDialog) {
        CitySelectionDialog(
            currentCityName = currentLocation.cityName,
            onSelectCity = { city ->
                viewModel.selectCity(city)
                showCityDialog = false
            },
            onDismiss = { showCityDialog = false }
        )
    }
}

@Composable
fun IslamicAppHeader(
    appName: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Circular Islamic Logo Emblem
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(2.dp, GoldAccent, CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_islamic_logo_1786916295429),
                        contentDescription = "Islamic Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column {
                    Text(
                        text = appName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "بِسْمِ اللَّـهِ الرَّحْمَـٰنِ الرَّحِيمِ",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = GoldAccent
                    )
                }
            }

            // Decorative Islamic Crescent or Mosque Icon
            Icon(
                imageVector = Icons.Default.Mosque,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun LocationBar(
    cityName: String,
    countryName: String,
    isGps: Boolean,
    isLocating: Boolean,
    onDetectGps: () -> Unit,
    onChangeCity: () -> Unit
) {
    val strings = LocalAppStrings.current

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onChangeCity() }
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isLocating) strings.searchingLocation else "$cityName, $countryName",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isGps) strings.autoGps else strings.changeLocation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            IconButton(
                onClick = onDetectGps,
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    .testTag("gps_button")
            ) {
                if (isLocating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = strings.autoGps,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HeroPrayerCountdownCard(
    schedule: DayPrayerSchedule,
    onTestAthan: () -> Unit
) {
    val strings = LocalAppStrings.current
    val nextPrayer = schedule.nextPrayer

    val prayerName = when (nextPrayer?.type) {
        PrayerType.FAJR -> strings.fajr
        PrayerType.SUNRISE -> strings.sunrise
        PrayerType.DHUHR -> strings.dhuhr
        PrayerType.ASR -> strings.asr
        PrayerType.MAGHRIB -> strings.maghrib
        PrayerType.ISHA -> strings.isha
        null -> ""
    }

    val hours = schedule.remainingSeconds / 3600
    val minutes = (schedule.remainingSeconds % 3600) / 60
    val seconds = schedule.remainingSeconds % 60
    val formattedCountdown = String.format("%02d:%02d:%02d", hours, minutes, seconds)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                    )
                )
            )
    ) {
        // Islamic Architecture Background Banner with Darkness Overlay
        Image(
            painter = painterResource(id = R.drawable.img_islamic_banner_1786916307487),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        // Gradient tint overlay to keep text highly legible
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.65f),
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hijri Date
            Text(
                text = schedule.hijriDate,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = GoldAccent,
                textAlign = TextAlign.Center
            )

            Text(
                text = schedule.gregorianDate,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Next Prayer Title
            Text(
                text = "${strings.nextPrayer}: $prayerName",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            if (nextPrayer != null) {
                Text(
                    text = nextPrayer.timeFormatted,
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldLight
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Large Digital Countdown Pill
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.Black.copy(alpha = 0.25f),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = null,
                        tint = GoldLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "$formattedCountdown - ${strings.remainingTime}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun QuickShortcutsRow(
    onOpenKahf: () -> Unit,
    onOpenMulk: () -> Unit,
    onOpenYasin: () -> Unit,
    onOpenQibla: () -> Unit,
    onOpenTasbeeh: () -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        item {
            ShortcutChip(
                label = "سورة الكهف",
                icon = Icons.Outlined.MenuBook,
                color = EmeraldPrimary,
                onClick = onOpenKahf
            )
        }
        item {
            ShortcutChip(
                label = "سورة الملك",
                icon = Icons.Outlined.AutoStories,
                color = EmeraldPrimary,
                onClick = onOpenMulk
            )
        }
        item {
            ShortcutChip(
                label = "سورة يس",
                icon = Icons.Outlined.BookmarkBorder,
                color = EmeraldPrimary,
                onClick = onOpenYasin
            )
        }
        item {
            ShortcutChip(
                label = "بوصلة القبلة",
                icon = Icons.Outlined.Explore,
                color = GoldAccent,
                onClick = onOpenQibla
            )
        }
        item {
            ShortcutChip(
                label = "السبحة الإلكترونية",
                icon = Icons.Outlined.TouchApp,
                color = EmeraldLight,
                onClick = onOpenTasbeeh
            )
        }
    }
}

@Composable
fun ShortcutChip(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun PrayerCardItem(
    prayer: SinglePrayer,
    alertMode: PrayerAlertMode,
    onToggleAlertMode: (PrayerAlertMode) -> Unit
) {
    val strings = LocalAppStrings.current

    val (name, icon) = when (prayer.type) {
        PrayerType.FAJR -> Pair(strings.fajr, Icons.Outlined.WbTwilight)
        PrayerType.SUNRISE -> Pair(strings.sunrise, Icons.Outlined.WbSunny)
        PrayerType.DHUHR -> Pair(strings.dhuhr, Icons.Outlined.LightMode)
        PrayerType.ASR -> Pair(strings.asr, Icons.Outlined.WbCloudy)
        PrayerType.MAGHRIB -> Pair(strings.maghrib, Icons.Outlined.NightsStay)
        PrayerType.ISHA -> Pair(strings.isha, Icons.Outlined.Bedtime)
    }

    val isNext = prayer.isNext
    val cardBg = if (isNext) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    val textColor = if (isNext) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isNext) 4.dp else 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isNext) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isNext) Color.White else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = textColor
                    )
                    if (isNext) {
                        Text(
                            text = strings.nextPrayer,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = prayer.timeFormatted,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = textColor
                )

                // Sound Alert Toggle
                IconButton(
                    onClick = { onToggleAlertMode(alertMode) },
                    modifier = Modifier.size(36.dp)
                ) {
                    val alertIcon = when (alertMode) {
                        PrayerAlertMode.FULL_ATHAN -> Icons.Default.VolumeUp
                        PrayerAlertMode.TAKBEER_ONLY -> Icons.Default.NotificationsActive
                        PrayerAlertMode.GENTLE_TONE -> Icons.Default.Notifications
                        PrayerAlertMode.SILENT -> Icons.Default.VolumeOff
                    }
                    val alertTint = if (alertMode == PrayerAlertMode.SILENT) {
                        MaterialTheme.colorScheme.outline
                    } else {
                        GoldAccent
                    }
                    Icon(
                        imageVector = alertIcon,
                        contentDescription = null,
                        tint = alertTint,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DailyInspirationCard() {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "آية كريمة",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "﴿ أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ ﴾",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "سورة الرعد - آية 28",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CitySelectionDialog(
    currentCityName: String,
    onSelectCity: (CityPreset) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredCities = WORLD_CITIES.filter {
        it.nameEn.contains(searchQuery, ignoreCase = true) ||
        it.nameAr.contains(searchQuery, ignoreCase = true) ||
        it.countryEn.contains(searchQuery, ignoreCase = true) ||
        it.countryAr.contains(searchQuery, ignoreCase = true)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = "اختر المدينة / Select City",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("بحث عن مدينة أو دولة...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredCities) { city ->
                        val isSelected = city.nameEn.equals(currentCityName, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectCity(city) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${city.nameAr} (${city.nameEn})",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${city.countryAr} - ${city.countryEn}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Interactive 3D Islamic Interface Showcase Card
 */
@Composable
fun Islamic3dShowcaseCard(
    selectedSceneIndex: Int,
    onSelectScene: (Int) -> Unit,
    onNavigateToQuran: () -> Unit,
    onNavigateToQibla: () -> Unit,
    onNavigateToTasbeeh: () -> Unit,
    modifier: Modifier = Modifier
) {
    data class SceneInfo(
        val title: String,
        val subtitle: String,
        val verseText: String,
        val actionText: String,
        val actionIcon: ImageVector,
        val imageRes: Int,
        val onAction: () -> Unit
    )

    val scenes = listOf(
        SceneInfo(
            title = "الكعبة المشرفة 3D",
            subtitle = "رحاب المسجد الحرام وقبلة المسلمين",
            verseText = "« جَعَلَ اللَّهُ الْكَعْبَةَ الْبَيْتَ الْحَرَامَ قِيَامًا لِّلنَّاسِ »",
            actionText = "بوصلة القبلة 3D",
            actionIcon = Icons.Default.Explore,
            imageRes = R.drawable.islamic_3d_kaaba_scene_1786962150020,
            onAction = onNavigateToQibla
        ),
        SceneInfo(
            title = "المصحف الشريف 3D",
            subtitle = "نور الهداية والتلاوات العطرة",
            verseText = "« إِنَّ هَـٰذَا الْقُرْآنَ يَهْدِي لِلَّتِي هِيَ أَقْوَمُ »",
            actionText = "تلاوة القرآن الكريم",
            actionIcon = Icons.Default.MenuBook,
            imageRes = R.drawable.islamic_3d_quran_dome_1786962161749,
            onAction = onNavigateToQuran
        ),
        SceneInfo(
            title = "المآذن والهلال 3D",
            subtitle = "خشوع المساجد والذكر الحكيم",
            verseText = "« فِي بُيُوتٍ أَذِنَ اللَّهُ أَن تُرْفَعَ وَيُذْكَرَ فِيهَا اسْمُهُ »",
            actionText = "السبحة والأذكار",
            actionIcon = Icons.Default.TouchApp,
            imageRes = R.drawable.img_islamic_banner_1786916307487,
            onAction = onNavigateToTasbeeh
        )
    )

    val safeIndex = selectedSceneIndex.coerceIn(0, scenes.size - 1)
    val activeScene = scenes[safeIndex]

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header with 3D badge and interactive scene tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(GoldAccent, Color(0xFFE59838))
                                )
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "3D إسلامي مجسم",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black
                        )
                    }
                    Text(
                        text = "المشاهد التفاعلية",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Scene switcher tabs
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    scenes.indices.forEach { index ->
                        val isSelected = index == safeIndex
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 28.dp else 22.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) GoldAccent else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                )
                                .clickable { onSelectScene(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = if (isSelected) 12.sp else 10.sp
                                ),
                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 3D Scene Hero Banner with rich depth overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = activeScene.imageRes),
                    contentDescription = activeScene.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Depth gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Black.copy(alpha = 0.85f)
                                )
                            )
                        )
                )

                // Floating 3D scene title and quote
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top tag
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.7f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = activeScene.title,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Bottom verse and action
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = activeScene.verseText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            ),
                            color = GoldLight,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = activeScene.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Bottom quick action button
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { activeScene.onAction() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = activeScene.actionIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = activeScene.actionText,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Quick Switcher Bar for Background Styles & Color Themes
 */
@Composable
fun BackgroundAndThemeQuickBar(
    currentBackground: AppBackgroundStyle,
    currentPalette: AppThemePalette,
    onSelectBackground: (AppBackgroundStyle) -> Unit,
    onSelectPalette: (AppThemePalette) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Background Styles Header & Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Wallpaper,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "شكل وخلفية التطبيق 3D",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(AppBackgroundStyle.entries) { style ->
                    val isSelected = style == currentBackground
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier.clickable { onSelectBackground(style) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val styleRes = style.drawableResId
                            if (styleRes != null) {
                                Surface(
                                    shape = CircleShape,
                                    modifier = Modifier.size(16.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = styleRes),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .background(
                                            if (style == AppBackgroundStyle.SPIRITUAL_AURA) GoldAccent else MaterialTheme.colorScheme.primary,
                                            CircleShape
                                        )
                                )
                            }
                            Text(
                                text = style.nameAr,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Theme Color Palettes Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ColorLens,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "ألوان السمة",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(AppThemePalette.entries) { palette ->
                    val isSelected = palette == currentPalette
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier.clickable { onSelectPalette(palette) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(palette.previewGradient)
                                    )
                            )
                            Text(
                                text = palette.nameAr,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
