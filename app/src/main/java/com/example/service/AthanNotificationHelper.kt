package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.data.prayer.PrayerAlertMode
import com.example.data.prayer.PrayerType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Muezzin(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val audioUrl: String
)

val MUEZZIN_LIST = listOf(
    Muezzin(
        id = "makkah",
        nameAr = "أذان الحرم المكي (مكة المكرمة)",
        nameEn = "Makkah Haram Athan",
        audioUrl = "https://media.sd.ma/assabile/athan/athan-makkah.mp3"
    ),
    Muezzin(
        id = "madinah",
        nameAr = "أذان المسجد النبوي (المدينة المنورة)",
        nameEn = "Madinah Nabawi Athan",
        audioUrl = "https://media.sd.ma/assabile/athan/athan-madina.mp3"
    ),
    Muezzin(
        id = "aqsa",
        nameAr = "أذان المسجد الأقصى المبارك",
        nameEn = "Al-Aqsa Mosque Athan",
        audioUrl = "https://media.sd.ma/assabile/athan/athan-al-aqsa.mp3"
    ),
    Muezzin(
        id = "alafasy",
        nameAr = "أذان مشاري راشد العفاسي",
        nameEn = "Mishary Rashid Alafasy",
        audioUrl = "https://media.sd.ma/assabile/athan/athan-alafasy.mp3"
    ),
    Muezzin(
        id = "abdulbasit",
        nameAr = "أذان عبد الباسط عبد الصمد",
        nameEn = "Abdulbasit Abdussamad",
        audioUrl = "https://media.sd.ma/assabile/athan/athan-abdelbasset-abdessamad.mp3"
    )
)

class AthanNotificationHelper(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private var athanPlayer: MediaPlayer? = null

    private val _isPlayingAthan = MutableStateFlow(false)
    val isPlayingAthan = _isPlayingAthan.asStateFlow()

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Prayer Times & Athan",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for Islamic prayer times and Athan"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun playAthanPreview(muezzin: Muezzin) {
        stopAthan()
        try {
            _isPlayingAthan.value = true
            athanPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                )
                setDataSource(context, Uri.parse(muezzin.audioUrl))
                setOnPreparedListener { mp ->
                    mp.start()
                }
                setOnCompletionListener {
                    _isPlayingAthan.value = false
                    stopAthan()
                }
                setOnErrorListener { _, _, _ ->
                    _isPlayingAthan.value = false
                    false
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("AthanNotification", "Error playing athan", e)
            _isPlayingAthan.value = false
        }
    }

    fun stopAthan() {
        try {
            athanPlayer?.stop()
            athanPlayer?.release()
        } catch (e: Exception) {
            Log.e("AthanNotification", "Error stopping athan", e)
        } finally {
            athanPlayer = null
            _isPlayingAthan.value = false
        }
    }

    fun showPrayerNotification(prayerName: String, prayerTime: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("حان الآن موعد $prayerName")
            .setContentText("وقت صلاة $prayerName: $prayerTime")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "athan_channel_noor_al_iman"
        const val NOTIFICATION_ID = 1001
    }
}
