package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.data.quran.Surah
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.LocalAppStrings
import com.example.ui.localization.getAppStrings
import com.example.ui.screens.azkar.AzkarScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.qibla.QiblaScreen
import com.example.ui.screens.quran.QuranAudioBar
import com.example.ui.screens.quran.QuranScreen
import com.example.ui.screens.quran.SurahReaderScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.theme.NoorAlImanTheme
import com.example.ui.viewmodel.MainViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

enum class AppTab(
    val titleKey: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("homeTab", Icons.Filled.AccessTime, Icons.Outlined.AccessTime),
    QURAN("quranTab", Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
    QIBLA("qiblaTab", Icons.Filled.Explore, Icons.Outlined.Explore),
    AZKAR("azkarTab", Icons.Filled.TouchApp, Icons.Outlined.TouchApp),
    SETTINGS("settingsTab", Icons.Filled.Settings, Icons.Outlined.Settings)
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoorAlImanApp(viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NoorAlImanApp(viewModel: MainViewModel) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isDarkThemePreference by viewModel.isDarkTheme.collectAsState()
    val themePalette by viewModel.themePalette.collectAsState()

    val darkTheme = isDarkThemePreference ?: isSystemInDarkTheme()
    val strings = remember(currentLanguage) { getAppStrings(currentLanguage) }
    val layoutDirection = if (currentLanguage == AppLanguage.ARABIC || currentLanguage == AppLanguage.URDU) {
        LayoutDirection.Rtl
    } else {
        LayoutDirection.Ltr
    }

    // Request permissions automatically on start
    val permissionsToRequest = mutableListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val permissionsState = rememberMultiplePermissionsState(permissions = permissionsToRequest) { results ->
        if (results[Manifest.permission.ACCESS_FINE_LOCATION] == true || results[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            viewModel.detectGpsLocation()
        }
    }

    LaunchedEffect(Unit) {
        permissionsState.launchMultiplePermissionRequest()
    }

    var currentTab by remember { mutableStateOf(AppTab.HOME) }
    var viewingSurah by remember { mutableStateOf<Surah?>(null) }

    CompositionLocalProvider(
        LocalAppStrings provides strings,
        LocalLayoutDirection provides layoutDirection
    ) {
        NoorAlImanTheme(palette = themePalette, darkTheme = darkTheme) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                if (viewingSurah != null) {
                    SurahReaderScreen(
                        surah = viewingSurah!!,
                        viewModel = viewModel,
                        onBack = { viewingSurah = null }
                    )
                } else {
                    Scaffold(
                        bottomBar = {
                            Column {
                                // Floating Quran Audio Mini-Player
                                QuranAudioBar(viewModel = viewModel)

                                // Navigation Bar
                                NavigationBar(
                                    modifier = Modifier.testTag("bottom_navigation_bar")
                                ) {
                                    AppTab.entries.forEach { tab ->
                                        val isSelected = currentTab == tab
                                        val labelText = when (tab) {
                                            AppTab.HOME -> strings.homeTab
                                            AppTab.QURAN -> strings.quranTab
                                            AppTab.QIBLA -> strings.qiblaTab
                                            AppTab.AZKAR -> strings.azkarTab
                                            AppTab.SETTINGS -> strings.settingsTab
                                        }

                                        NavigationBarItem(
                                            selected = isSelected,
                                            onClick = { currentTab = tab },
                                            icon = {
                                                Icon(
                                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                                    contentDescription = labelText
                                                )
                                            },
                                            label = {
                                                Text(
                                                    text = labelText,
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            },
                                            modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            AnimatedContent(
                                targetState = currentTab,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "tab_transition"
                            ) { targetTab ->
                                when (targetTab) {
                                    AppTab.HOME -> HomeScreen(
                                        viewModel = viewModel,
                                        onNavigateToQuranSurah = { surahNumber ->
                                            val s = viewModel.surahs.firstOrNull { it.number == surahNumber }
                                            if (s != null) {
                                                viewingSurah = s
                                            }
                                        },
                                        onNavigateToQibla = { currentTab = AppTab.QIBLA },
                                        onNavigateToTasbeeh = { currentTab = AppTab.AZKAR }
                                    )
                                    AppTab.QURAN -> QuranScreen(
                                        viewModel = viewModel,
                                        onOpenSurah = { surah -> viewingSurah = surah }
                                    )
                                    AppTab.QIBLA -> QiblaScreen(viewModel = viewModel)
                                    AppTab.AZKAR -> AzkarScreen(viewModel = viewModel)
                                    AppTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

