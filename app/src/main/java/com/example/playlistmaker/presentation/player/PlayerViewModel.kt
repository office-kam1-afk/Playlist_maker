package com.example.playlistmaker.presentation.player

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.entity.Track
import java.util.Locale

class PlayerViewModel : ViewModel() {

    private val _screenState = MutableLiveData(PlayerScreenState())
    val screenState: LiveData<PlayerScreenState> = _screenState

    private var mediaPlayer: MediaPlayer? = null
    private var isPrepared = false
    private var currentPosition = 0

    private val handler = Handler(Looper.getMainLooper())
    private val progressRunnable = object : Runnable {
        override fun run() {
            val mp = mediaPlayer ?: return
            val state = _screenState.value ?: return
            if (state.isPlaying && isPrepared) {
                val pos = mp.currentPosition
                val progress = String.format(Locale.getDefault(), "%02d:%02d", pos / 60000, (pos % 60000) / 1000)
                _screenState.value = state.copy(progressText = progress)
                handler.postDelayed(this, 500L)
            }
        }
    }

    fun initTrack(track: Track) {
        _screenState.value = PlayerScreenState(track = track, isTrackLoaded = true)
    }

    fun onPlayPauseClicked() {
        if (_screenState.value?.isPlaying == true) pausePlayback() else startPlayback()
    }

    fun pauseForLifecycle() {
        if (_screenState.value?.isPlaying == true) pausePlayback()
    }

    private fun startPlayback() {
        val state = _screenState.value ?: return
        val url = state.track?.previewUrl
        if (url.isNullOrEmpty()) {
            _screenState.value = state.copy(errorMessage = "Отрывок недоступен")
            return
        }
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(url)
                    setOnPreparedListener { mp ->
                        isPrepared = true
                        if (currentPosition > 0) mp.seekTo(currentPosition)
                        mp.start()
                        _screenState.value = (_screenState.value ?: state).copy(isPlaying = true, errorMessage = null)
                        startProgressUpdates()
                    }
                    setOnCompletionListener { stopPlayback() }
                    setOnErrorListener { _, _, _ ->
                        _screenState.value = (_screenState.value ?: state).copy(isPlaying = false, errorMessage = "Ошибка воспроизведения")
                        stopPlayback()
                        true
                    }
                    prepareAsync()
                }
            } else {
                if (!isPrepared) return
                mediaPlayer?.start()
                _screenState.value = state.copy(isPlaying = true, errorMessage = null)
                startProgressUpdates()
            }
        } catch (e: Exception) {
            _screenState.value = state.copy(isPlaying = false, errorMessage = "Ошибка: ${e.message}")
            stopPlayback()
        }
    }

    private fun pausePlayback() {
        mediaPlayer?.let { mp ->
            if (!isPrepared) { stopPlayback(); return }
            if (mp.isPlaying) {
                currentPosition = mp.currentPosition
                mp.pause()
            }
        }
        _screenState.value = (_screenState.value ?: return).copy(isPlaying = false)
        stopProgressUpdates()
    }

    private fun stopPlayback() {
        stopProgressUpdates()
        isPrepared = false
        currentPosition = 0
        mediaPlayer?.let { mp ->
            runCatching { if (mp.isPlaying) mp.stop() }
            runCatching { mp.reset() }
            runCatching { mp.release() }
        }
        mediaPlayer = null
        _screenState.value = (_screenState.value ?: return).copy(isPlaying = false, progressText = "00:00")
    }

    private fun startProgressUpdates() {
        handler.removeCallbacks(progressRunnable)
        handler.post(progressRunnable)
    }

    private fun stopProgressUpdates() { handler.removeCallbacks(progressRunnable) }

    override fun onCleared() {
        super.onCleared()
        stopPlayback()
    }
}