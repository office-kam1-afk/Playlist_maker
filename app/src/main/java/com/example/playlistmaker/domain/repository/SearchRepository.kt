package com.example.playlistmaker.domain.repository
import com.example.playlistmaker.domain.entity.Track
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchTracks(query: String): Flow<List<Track>>
}