package com.example.playlistmaker.presentation.player

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.interactor.FavoritesInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Locale

class PlayerViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _screenState = MutableLiveData(PlayerScreenState())
    val screenState: LiveData<PlayerScreenState> = _screenState

    private var mediaPlayer: MediaPlayer? = null
    private var isPrepared = false
    private var currentPosition = 0
    private var progressJob: Job? = null
    private var favoritesJob: Job? = null
    private var currentTrackId: Long? = null

    fun initTrack(track: Track) {
        currentTrackId = track.trackId
        _screenState.value = PlayerScreenState(
            track = track,
            isTrackLoaded = true
        )
        observeFavorites()
    }

    private fun observeFavorites() {
        favoritesJob?.cancel()
        favoritesJob = viewModelScope.launch {
            favoritesInteractor.getFavorites()
                .catch { /* логирование при необходимости */ }
                .collect { favoriteTracks ->
                    val isFavorite = favoriteTracks.any { it.trackId == currentTrackId }
                    val currentState = _screenState.value
                    if (currentState != null && currentState.isFavorite != isFavorite) {
                        _screenState.value = currentState.copy(isFavorite = isFavorite)
                    }
                }
        }
    }

    fun onFavoriteClicked() {
        val track = _screenState.value?.track ?: return
        viewModelScope.launch {
            val isCurrentlyFavorite = _screenState.value?.isFavorite ?: false
            if (isCurrentlyFavorite) {
                favoritesInteractor.removeFromFavorites(track)
            } else {
                favoritesInteractor.addToFavorites(track)
            }
        }
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
                        _screenState.value = (_screenState.value ?: state).copy(
                            isPlaying = true,
                            errorMessage = null
                        )
                        startProgressUpdates()
                    }
                    setOnCompletionListener { stopPlayback() }
                    setOnErrorListener { _, _, _ ->
                        _screenState.value = (_screenState.value ?: state).copy(
                            isPlaying = false,
                            errorMessage = "Ошибка воспроизведения"
                        )
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
            _screenState.value = state.copy(
                isPlaying = false,
                errorMessage = "Ошибка: ${e.message}"
            )
            stopPlayback()
        }
    }

    private fun pausePlayback() {
        mediaPlayer?.let { mp ->
            if (!isPrepared) {
                stopPlayback()
                return
            }
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
        _screenState.value = (_screenState.value ?: return).copy(
            isPlaying = false,
            progressText = "00:00"
        )
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (_screenState.value?.isPlaying == true) {
                delay(300L)
                val mp = mediaPlayer ?: break
                val pos = mp.currentPosition
                val progress = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    pos / 60000,
                    (pos % 60000) / 1000
                )
                _screenState.value = _screenState.value?.copy(progressText = progress)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        progressJob?.cancel()
        favoritesJob?.cancel()
        stopPlayback()
    }
}