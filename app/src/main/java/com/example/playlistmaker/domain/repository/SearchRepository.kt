package com.example.playlistmaker.domain.repository
import com.example.playlistmaker.domain.entity.Track


interface SearchRepository {
    suspend fun searchTracks(query: String): List<Track>
}