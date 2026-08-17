package com.example.playlistmaker.domain.repository
import com.example.playlistmaker.domain.entity.Track
import retrofit2.http.Query

interface SearchRepository {
    suspend fun searchTracks(query: String): List<Track>
}