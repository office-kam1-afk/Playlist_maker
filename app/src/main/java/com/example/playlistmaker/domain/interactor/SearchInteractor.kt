package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.entity.Track
import kotlinx.coroutines.flow.Flow
interface SearchInteractor {
    fun searchTracks(query: String): Flow<List<Track>>
}