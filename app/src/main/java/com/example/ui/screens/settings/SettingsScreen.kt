package com.example.ui.screens.settings

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.prayer.CalculationMethod
import com.example.service.MUEZZIN_LIST
import com.example.service.Muezzin
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.LocalAppStrings
import com.example.ui.theme.AppThemePalette
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val calculationMethod by viewModel.calculationMethod.collectAsState()
    val selectedMuezzin by viewModel.selectedMuezzin.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val themePalette by viewModel.themePalette.collectAsState()
    val customAppName by viewModel.customAppName.collectAsState()
    val isPlayingAthan by viewModel.athanNotificationHelper.isPlayingAthan.collectAsState()

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showMethodDialog by remember { mutableStateOf(false) }
    var showMuezzinDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showAppNameDialog by remember { mutableStateOf(false) }
    var showPublishGuideDialog by remember { mutableStateOf(false) }

    val isArabic = currentLanguage == AppLanguage.ARABIC

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = strings.settingsTitle,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        // 1. Color Palette Customization
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showThemeDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ColorLens,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                        Column {
                            Text(
                                text = strings.themeColor,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (isArabic) themePalette.nameAr else themePalette.nameEn,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Palette color circle preview
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        themePalette.previewGradient.forEach { col ->
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(col, CircleShape)
                                    .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }
        }

        // 2. Custom App Name
        item {
            SettingsCardItem(
                title = strings.customizeAppName,
                subtitle = customAppName,
                icon = Icons.Outlined.Edit,
                onClick = { showAppNameDialog = true }
            )
        }

        // 3. Language Setting
        item {
            SettingsCardItem(
                title = strings.language,
                subtitle = "${currentLanguage.nativeName} (${currentLanguage.displayName})",
                icon = Icons.Outlined.Language,
                onClick = { showLanguageDialog = true }
            )
        }

        // 4. Calculation Method Setting
        item {
            SettingsCardItem(
                title = strings.calculationMethod,
                subtitle = calculationMethod.title,
                icon = Icons.Default.Calculate,
                onClick = { showMethodDialog = true }
            )
        }

        // 5. Muezzin Voice Setting + Audio Preview
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showMuezzinDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = strings.athanSound,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = selectedMuezzin.nameAr,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Athan Preview Button
                    IconButton(
                        onClick = {
                            if (isPlayingAthan) {
                                viewModel.athanNotificationHelper.stopAthan()
                            } else {
                                viewModel.athanNotificationHelper.playAthanPreview(selectedMuezzin)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isPlayingAthan) Icons.Outlined.StopCircle else Icons.Outlined.PlayCircle,
                            contentDescription = "Test Athan",
                            tint = GoldAccent,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        // 6. Dark Theme Setting
        item {
            SettingsCardItem(
                title = strings.darkMode,
                subtitle = when (isDarkTheme) {
                    true -> "الوضع الداكن (Dark)"
                    false -> "الوضع الفاتح (Light)"
                    null -> strings.followSystem
                },
                icon = Icons.Outlined.Palette,
                onClick = {
                    val next = when (isDarkTheme) {
                        null -> true
                        true -> false
                        false -> null
                    }
                    viewModel.setDarkTheme(next)
                }
            )
        }

        // 7. Store Publishing & Export Guide Banner Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPublishGuideDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CloudUpload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = strings.publishToStore,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = strings.publishToStoreDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // 8. App Info & Version
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = customAppName,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$customAppName • الإصدار 1.0.0 (Production Ready)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "تطبيق إسلامي شامل ودقيق لمواقيت الصلاة والقرآن الكريم والقبلة والأذكار",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    // ----------------- DIALOGS -----------------

    // Theme Color Picker Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = {
                Text(
                    text = strings.selectThemeColor,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(AppThemePalette.entries) { palette ->
                        val isSelected = palette == themePalette
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setThemePalette(palette)
                                    showThemeDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Gradient Circle preview
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(palette.previewGradient)
                                            )
                                            .border(1.5.dp, Color.White, CircleShape)
                                    )

                                    Column {
                                        Text(
                                            text = if (isArabic) palette.nameAr else palette.nameEn,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        )
                                        Text(
                                            text = if (isArabic) palette.nameEn else palette.nameAr,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }

    // App Name Customization Dialog
    if (showAppNameDialog) {
        var tempName by remember { mutableStateOf(customAppName) }
        val presets = listOf(
            "نور الإيمان",
            "زاد المسلم",
            "صلاتي وقرآني",
            "مسلم برو",
            "هدى ونور",
            "قبس الإيمان",
            "Noor Al-Iman",
            "Muslim Companion"
        )

        AlertDialog(
            onDismissRequest = { showAppNameDialog = false },
            title = {
                Text(
                    text = strings.changeAppNameTitle,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text(strings.enterAppNameHint) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Text(
                        text = strings.quickPresets,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(presets) { preset ->
                            SuggestionChip(
                                onClick = { tempName = preset },
                                label = { Text(preset, style = MaterialTheme.typography.bodySmall) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempName.isNotBlank()) {
                            viewModel.setCustomAppName(tempName)
                        }
                        showAppNameDialog = false
                    }
                ) {
                    Text(strings.save)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAppNameDialog = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }

    // Publishing & Store Guide Dialog
    if (showPublishGuideDialog) {
        AlertDialog(
            onDismissRequest = { showPublishGuideDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "دليل نشر التطبيق على المتاجر",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "1️⃣ تصدير ملف التطبيق (APK / AAB)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "• يمكنك تصدير وتنزيل ملف APK أو AAB مباشرة من قائمة الإعدادات (Settings / Export) في منصة Google AI Studio.\n• كما يمكنك تصدير المشروع كاملاً كملف ZIP أو رفعه مباشرة إلى GitHub.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "2️⃣ النشر على Google Play Store",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "• افتح حساب Google Play Console.\n• أنشئ تطبيقاً جديداً وأدخل الاسم، الوصف، ولقطات الشاشة.\n• قم برفع حزمة التطبيق الموقعة (.aab).\n• أكمل استبيان تصنيف المحتوى وسياسة الخصوصية ثم أرسل التطبيق للمراجعة.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "3️⃣ النشر على Apple App Store",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "• تطبيق أندرويد مبني بـ Kotlin و Jetpack Compose.\n• لنشر نسخة iOS على App Store، يتم تصدير المشروع كـ ZIP وفتح المنظومة عبر Kotlin Multiplatform (Compose Multiplatform) أو Swift في Xcode مع حساب Apple Developer.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showPublishGuideDialog = false }) {
                    Text("فهمت ذلك")
                }
            }
        )
    }

    // Language Dialog
    if (showLanguageDialog) {
        val languages = AppLanguage.entries.toList()
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(strings.selectLanguage, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(languages) { lang ->
                        val isSelected = lang == currentLanguage
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setLanguage(lang)
                                    showLanguageDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${lang.nativeName} (${lang.displayName})",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }

    // Calculation Method Dialog
    if (showMethodDialog) {
        val methods = CalculationMethod.entries.toList()
        AlertDialog(
            onDismissRequest = { showMethodDialog = false },
            title = { Text(strings.calculationMethod, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(methods) { method ->
                        val isSelected = method == calculationMethod
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setCalculationMethod(method)
                                    showMethodDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = method.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMethodDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }

    // Muezzin Dialog
    if (showMuezzinDialog) {
        AlertDialog(
            onDismissRequest = { showMuezzinDialog = false },
            title = { Text(strings.athanSound, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(MUEZZIN_LIST) { muezzin ->
                        val isSelected = muezzin.id == selectedMuezzin.id
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setMuezzin(muezzin)
                                    showMuezzinDialog = false
                                }
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
                                        text = muezzin.nameAr,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                    Text(
                                        text = muezzin.nameEn,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMuezzinDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }
}

@Composable
fun SettingsCardItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}
