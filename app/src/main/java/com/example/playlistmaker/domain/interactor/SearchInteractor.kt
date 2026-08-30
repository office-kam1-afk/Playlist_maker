package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.entity.Track
import retrofit2.http.Query

interface SearchInteractor {
    suspend fun searchTracks(query: String): List<Track>
}