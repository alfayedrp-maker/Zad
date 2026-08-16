package com.example.ui.screens.qibla

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.localization.LocalAppStrings
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.MainViewModel
import kotlin.math.*

@Composable
fun QiblaScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current

    val schedule by viewModel.prayerSchedule.collectAsState()
    val heading by viewModel.azimuthHeading.collectAsState()
    val isSensorAvailable by viewModel.isSensorAvailable.collectAsState()

    val qiblaBearing = schedule?.qiblaBearing ?: 0.0
    val distanceKm = schedule?.distanceToMakkahKm ?: 0

    // Angle offset: positive or negative
    var diff = (qiblaBearing - heading).toFloat()
    while (diff > 180f) diff -= 360f
    while (diff < -180f) diff += 360f

    val isAligned = abs(diff) <= 3f

    // Trigger haptic feedback when aligned
    LaunchedEffect(isAligned) {
        if (isAligned) {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator?.vibrate(80)
            }
        }
    }

    DisposableEffect(Unit) {
        viewModel.startQiblaSensor()
        onDispose {
            viewModel.stopQiblaSensor()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top status card
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                text = strings.qiblaTitle,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isAligned) strings.qiblaAligned else strings.pointToKaaba,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isAligned) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isAligned) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        // Animated 360 Qibla Compass Dial
        Box(
            modifier = Modifier
                .size(300.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            QiblaCompassCanvas(
                heading = heading,
                qiblaBearing = qiblaBearing.toFloat(),
                isAligned = isAligned
            )

            // Center status circle
            Surface(
                modifier = Modifier.size(68.dp),
                shape = CircleShape,
                color = if (isAligned) EmeraldPrimary else MaterialTheme.colorScheme.surface,
                shadowElevation = 6.dp,
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    if (isAligned) GoldAccent else MaterialTheme.colorScheme.outline
                )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (isAligned) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    } else {
                        Text(
                            text = "${heading.roundToInt()}°",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Bottom Distance and Angle Metrics Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 80.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$qiblaBearing°",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = strings.degreesFromNorth,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Divider(
                    modifier = Modifier
                        .height(36.dp)
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$distanceKm km",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = GoldAccent
                    )
                    Text(
                        text = strings.distanceToKaaba,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun QiblaCompassCanvas(
    heading: Float,
    qiblaBearing: Float,
    isAligned: Boolean
) {
    val dialRotation by animateFloatAsState(
        targetValue = -heading,
        animationSpec = spring(stiffness = 300f),
        label = "compass_rotation"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val outlineColor = MaterialTheme.colorScheme.outline
    val surfaceColor = MaterialTheme.colorScheme.surface

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f - 12.dp.toPx()

        // Outer Ring
        drawCircle(
            color = if (isAligned) GoldAccent else outlineColor.copy(alpha = 0.3f),
            radius = radius + 6.dp.toPx(),
            center = center,
            style = Stroke(width = if (isAligned) 4.dp.toPx() else 2.dp.toPx())
        )

        // Compass background gradient
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    surfaceColor,
                    if (isAligned) EmeraldPrimary.copy(alpha = 0.15f) else surfaceColor.copy(alpha = 0.8f)
                ),
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center
        )

        // Rotatable Dial (North, South, East, West, Degree Ticks, and Kaaba Pointer)
        rotate(dialRotation, center) {
            // Degree tick lines
            for (angle in 0 until 360 step 15) {
                val isMajor = angle % 90 == 0
                val tickLength = if (isMajor) 14.dp.toPx() else 7.dp.toPx()
                val tickWidth = if (isMajor) 2.5.dp.toPx() else 1.dp.toPx()
                val tickColor = if (angle == 0) Color.Red else outlineColor

                val rad = Math.toRadians(angle.toDouble())
                val startX = center.x + (radius - tickLength) * sin(rad).toFloat()
                val startY = center.y - (radius - tickLength) * cos(rad).toFloat()
                val endX = center.x + radius * sin(rad).toFloat()
                val endY = center.y - radius * cos(rad).toFloat()

                drawLine(
                    color = tickColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = tickWidth
                )
            }

            // North Pointer (Red Arrow)
            val northPath = Path().apply {
                moveTo(center.x, center.y - radius + 18.dp.toPx())
                lineTo(center.x - 7.dp.toPx(), center.y - radius + 34.dp.toPx())
                lineTo(center.x + 7.dp.toPx(), center.y - radius + 34.dp.toPx())
                close()
            }
            drawPath(northPath, color = Color.Red)

            // Qibla Pointer (Kaaba Golden Needle)
            rotate(qiblaBearing, center) {
                val qiblaPath = Path().apply {
                    moveTo(center.x, center.y - radius + 2.dp.toPx())
                    lineTo(center.x - 12.dp.toPx(), center.y - radius + 24.dp.toPx())
                    lineTo(center.x + 12.dp.toPx(), center.y - radius + 24.dp.toPx())
                    close()
                }
                drawPath(
                    qiblaPath,
                    brush = Brush.linearGradient(listOf(GoldAccent, GoldLight))
                )

                // Kaaba Symbol Box on dial
                drawRect(
                    color = Color.Black,
                    topLeft = Offset(center.x - 9.dp.toPx(), center.y - radius + 28.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(18.dp.toPx(), 18.dp.toPx())
                )
                // Kaaba Gold Band
                drawRect(
                    color = GoldAccent,
                    topLeft = Offset(center.x - 9.dp.toPx(), center.y - radius + 31.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(18.dp.toPx(), 4.dp.toPx())
                )
            }
        }
    }
}
