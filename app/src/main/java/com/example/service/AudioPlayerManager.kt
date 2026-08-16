package com.example.service

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class AudioPlaybackState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentSurah: Int = 1,
    val currentAyah: Int = 1,
    val currentReciterName: String = "",
    val currentTitle: String = "",
    val durationMs: Int = 0,
    val currentPositionMs: Int = 0,
    val error: String? = null
)

class AudioPlayerManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null

    private val _playbackState = MutableStateFlow(AudioPlaybackState())
    val playbackState: StateFlow<AudioPlaybackState> = _playbackState.asStateFlow()

    private var onAyahCompletedListener: (() -> Unit)? = null

    fun setOnAyahCompleted(listener: () -> Unit) {
        this.onAyahCompletedListener = listener
    }

    fun playAudioUrl(
        url: String,
        surahNumber: Int,
        ayahNumber: Int,
        reciterName: String,
        title: String
    ) {
        try {
            stopAudio()

            _playbackState.value = _playbackState.value.copy(
                isLoading = true,
                isPlaying = false,
                currentSurah = surahNumber,
                currentAyah = ayahNumber,
                currentReciterName = reciterName,
                currentTitle = title,
                error = null
            )

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(context, Uri.parse(url))
                setOnPreparedListener { mp ->
                    mp.start()
                    _playbackState.value = _playbackState.value.copy(
                        isLoading = false,
                        isPlaying = true,
                        durationMs = mp.duration
                    )
                    startProgressTracker()
                }
                setOnCompletionListener {
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = false,
                        currentPositionMs = 0
                    )
                    stopProgressTracker()
                    onAyahCompletedListener?.invoke()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("AudioPlayerManager", "MediaPlayer error: what=$what, extra=$extra")
                    _playbackState.value = _playbackState.value.copy(
                        isLoading = false,
                        isPlaying = false,
                        error = "Playback error"
                    )
                    false
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Failed to start audio", e)
            _playbackState.value = _playbackState.value.copy(
                isLoading = false,
                isPlaying = false,
                error = e.message
            )
        }
    }

    fun togglePlayPause() {
        mediaPlayer?.let { mp ->
            if (mp.isPlaying) {
                mp.pause()
                _playbackState.value = _playbackState.value.copy(isPlaying = false)
                stopProgressTracker()
            } else {
                mp.start()
                _playbackState.value = _playbackState.value.copy(isPlaying = true)
                startProgressTracker()
            }
        }
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.seekTo(positionMs)
        _playbackState.value = _playbackState.value.copy(currentPositionMs = positionMs)
    }

    fun stopAudio() {
        stopProgressTracker()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Error releasing player", e)
        } finally {
            mediaPlayer = null
            _playbackState.value = _playbackState.value.copy(
                isPlaying = false,
                isLoading = false,
                currentPositionMs = 0
            )
        }
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressJob = scope.launch {
            while (isActive) {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        _playbackState.value = _playbackState.value.copy(
                            currentPositionMs = mp.currentPosition,
                            durationMs = mp.duration
                        )
                    }
                }
                delay(500)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        stopAudio()
    }
}
