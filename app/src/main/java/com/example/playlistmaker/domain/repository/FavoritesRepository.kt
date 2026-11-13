package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.entity.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun addToFavorites(track: Track)
    suspend fun removeFromFavorites(track: Track)
    fun getFavorites(): Flow<List<Track>>
}