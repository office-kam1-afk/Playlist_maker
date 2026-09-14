package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.entity.Track

interface SearchInteractor {
    suspend fun searchTracks(query: String): List<Track>
}