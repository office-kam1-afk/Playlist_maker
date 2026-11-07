package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SearchInteractorImpl(
    private val repository: SearchRepository
) : SearchInteractor {

    override fun searchTracks(query: String): Flow<List<Track>> {
        val trimmedQuery = query.trim()
        return if (trimmedQuery.isBlank()) {
            flowOf(emptyList())
        } else {
            repository.searchTracks(trimmedQuery)
        }
    }
}