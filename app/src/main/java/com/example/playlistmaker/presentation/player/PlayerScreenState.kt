package com.example.playlistmaker.presentation.player

import android.os.Message
import com.example.playlistmaker.domain.entity.Track

data class PlayerScreenState(
    val track: Track? = null,
    val isPlaying: Boolean = false,
    val progressText: String = "00:00",
    val errorMessage: String? = null,
    val isTrackLoaded: Boolean = false
)
