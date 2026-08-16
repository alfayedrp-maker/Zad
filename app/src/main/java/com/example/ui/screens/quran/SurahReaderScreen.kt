package com.example.ui.screens.quran

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.quran.Ayah
import com.example.data.quran.Surah
import com.example.ui.localization.LocalAppStrings
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.WarmSand
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahReaderScreen(
    surah: Surah,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val ayahs by viewModel.activeAyahs.collectAsState()
    val isLoading by viewModel.isLoadingAyahs.collectAsState()
    val fontSize by viewModel.quranFontSize.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val playbackState by viewModel.audioPlaybackState.collectAsState()

    var showFontSlider by remember { mutableStateOf(false) }

    LaunchedEffect(surah.number) {
        viewModel.loadSurahAyahs(surah.number)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "سورة ${surah.nameArabic}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${surah.nameEnglish} • ${surah.numberOfAyahs} ${strings.versesCount}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFontSlider = !showFontSlider }) {
                        Icon(Icons.Default.FormatSize, contentDescription = strings.fontSize)
                    }
                    IconButton(onClick = { viewModel.playFullSurah(surah) }) {
                        Icon(Icons.Default.VolumeUp, contentDescription = strings.playAllSurah, tint = GoldAccent)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Font Size Zoom Slider Banner
            if (showFontSlider) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("A", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Slider(
                            value = fontSize,
                            onValueChange = { viewModel.setQuranFontSize(it) },
                            valueRange = 18f..38f,
                            modifier = Modifier.weight(1f)
                        )
                        Text("A", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Bismillah Banner (except Surah At-Tawbah 9)
                    if (surah.number != 9) {
                        item {
                            BismillahHeader()
                        }
                    }

                    items(ayahs) { ayah ->
                        val isCurrentlyPlaying = playbackState.isPlaying &&
                                playbackState.currentSurah == ayah.surahNumber &&
                                playbackState.currentAyah == ayah.numberInSurah

                        val isBookmarked = bookmarks.any { it.id == ayah.id }

                        AyahCard(
                            ayah = ayah,
                            fontSizeSp = fontSize,
                            isCurrentlyPlaying = isCurrentlyPlaying,
                            isBookmarked = isBookmarked,
                            onPlay = { viewModel.playAyahAudio(ayah) },
                            onToggleBookmark = {
                                viewModel.toggleBookmark(ayah, surah.nameArabic, surah.nameEnglish)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BismillahHeader() {
    val strings = LocalAppStrings.current
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = strings.bismillah,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = EmeraldPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AyahCard(
    ayah: Ayah,
    fontSizeSp: Float,
    isCurrentlyPlaying: Boolean,
    isBookmarked: Boolean,
    onPlay: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentlyPlaying) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrentlyPlaying) 4.dp else 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Ayah Action Header (Number badge, Play, Bookmark)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ayah Number Badge
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (isCurrentlyPlaying) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${ayah.numberInSurah}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isCurrentlyPlaying) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onPlay, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = if (isCurrentlyPlaying) Icons.Default.Pause else Icons.Outlined.PlayArrow,
                            contentDescription = "Play Ayah",
                            tint = if (isCurrentlyPlaying) EmeraldPrimary else GoldAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(onClick = onToggleBookmark, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Arabic Verse Text
            Text(
                text = "${ayah.textArabic} ﴿${ayah.numberInSurah}﴾",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = fontSizeSp.sp,
                    lineHeight = (fontSizeSp * 1.7f).sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right
            )

            // English Translation if available
            if (ayah.translationEnglish.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = ayah.translationEnglish,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = (fontSizeSp * 0.65f).coerceAtLeast(13f).sp,
                        lineHeight = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Left
                )
            }
        }
    }
}
