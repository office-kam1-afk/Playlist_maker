package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.repository.HistoryRepository
import com.example.playlistmaker.domain.repository.SearchRepository

class SearchInteractorImpl(
    private val repository: SearchRepository
) : SearchInteractor {
    override suspend fun searchTracks(query: String): List<Track> {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank()) return emptyList()
        return repository.searchTracks(trimmedQuery)
    }
}