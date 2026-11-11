package com.example.playlistmaker.presentation.library.favorites

import com.example.playlistmaker.domain.entity.Track

sealed interface FavoritesScreenState {
    object Empty : FavoritesScreenState
    data class Content(val tracks: List<Track>) : FavoritesScreenState
}