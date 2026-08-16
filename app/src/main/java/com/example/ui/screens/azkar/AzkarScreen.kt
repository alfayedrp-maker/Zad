package com.example.ui.screens.azkar

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.azkar.AzkarCategory
import com.example.data.azkar.AzkarRepository
import com.example.data.azkar.ZikrItem
import com.example.ui.localization.LocalAppStrings
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AzkarScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var selectedMainTab by remember { mutableIntStateOf(0) } // 0 = Hisn Al-Muslim Azkar, 1 = Digital Tasbeeh

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = strings.azkarTitle,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
        )

        // Main Tab Switcher: Azkar vs Tasbeeh
        TabRow(
            selectedTabIndex = selectedMainTab,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedMainTab]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            Tab(
                selected = selectedMainTab == 0,
                onClick = { selectedMainTab = 0 },
                text = { Text("حصن المسلم والأذكار", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedMainTab == 1,
                onClick = { selectedMainTab = 1 },
                text = { Text(strings.tasbeehCounter, fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedMainTab == 0) {
            AzkarCategoryView()
        } else {
            DigitalTasbeehView(viewModel = viewModel)
        }
    }
}

@Composable
fun AzkarCategoryView() {
    val strings = LocalAppStrings.current
    val categories = AzkarRepository.categories
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val currentCategory = categories[selectedCategoryIndex]

    // Local tap progress map for zikr items
    val progressMap = remember { mutableStateMapOf<String, Int>() }

    Column(modifier = Modifier.fillMaxSize()) {
        // Category Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(categories.size) { index ->
                val cat = categories[index]
                val isSelected = selectedCategoryIndex == index
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategoryIndex = index },
                    label = { Text(cat.titleArabic, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(currentCategory.items) { item ->
                val count = progressMap[item.id] ?: 0
                val isCompleted = count >= item.targetCount

                AzkarItemCard(
                    item = item,
                    currentCount = count,
                    isCompleted = isCompleted,
                    onTap = {
                        if (count < item.targetCount) {
                            progressMap[item.id] = count + 1
                        }
                    },
                    onReset = {
                        progressMap[item.id] = 0
                    }
                )
            }
        }
    }
}

@Composable
fun AzkarItemCard(
    item: ZikrItem,
    currentCount: Int,
    isCompleted: Boolean,
    onTap: () -> Unit,
    onReset: () -> Unit
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Benefit & Reset
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = item.benefit,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                if (currentCount > 0) {
                    IconButton(onClick = onReset, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Refresh, contentDescription = "Reset", modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Arabic Text
            Text(
                text = item.arabicText,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 19.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Translation
            Text(
                text = item.translation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Tap to Count Button
            Button(
                onClick = {
                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator?.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
                    }
                    onTap()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCompleted) EmeraldPrimary else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    if (isCompleted) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = GoldLight)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تم الإنجاز (${item.targetCount}/${item.targetCount})", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Outlined.TouchApp, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("اضغط للتسبيح ($currentCount / ${item.targetCount})", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DigitalTasbeehView(viewModel: MainViewModel) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    val tasbeehPresets = AzkarRepository.defaultTasbeehItems

    var selectedPresetIndex by remember { mutableIntStateOf(0) }
    val currentPreset = tasbeehPresets[selectedPresetIndex]

    val savedCounters by viewModel.tasbeehCounters.collectAsState()
    val savedEntity = savedCounters.firstOrNull { it.id == currentPreset.id }

    val currentCount = savedEntity?.count ?: 0
    val totalDone = savedEntity?.totalDone ?: 0
    val target = currentPreset.targetCount

    val progress = (currentCount.toFloat() / target.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 90.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Preset selector chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(tasbeehPresets.size) { index ->
                val preset = tasbeehPresets[index]
                val isSelected = selectedPresetIndex == index
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedPresetIndex = index },
                    label = { Text(preset.arabicText, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        // Active Zikr Title & Meaning
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            Text(
                text = currentPreset.arabicText,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = currentPreset.translation,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        // Big Circular Interactive Tap Counter Button
        Box(
            modifier = Modifier
                .size(230.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            EmeraldPrimary,
                            EmeraldDark
                        )
                    )
                )
                .clickable {
                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                    }
                    viewModel.incrementTasbeeh(
                        id = currentPreset.id,
                        arabicText = currentPreset.arabicText,
                        translation = currentPreset.translation,
                        target = target
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$currentCount",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 54.sp
                    ),
                    color = Color.White
                )
                Text(
                    text = "من $target",
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldLight
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = strings.tapToCount,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        // Bottom Stats & Reset
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${strings.totalDone}: $totalDone دورة",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "الهدف: $target تسبيحة لكل دورة",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { viewModel.resetTasbeeh(currentPreset.id) },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = strings.resetCounter,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
