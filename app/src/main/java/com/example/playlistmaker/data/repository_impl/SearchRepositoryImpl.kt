package com.example.playlistmaker.data.repository_impl

import com.example.playlistmaker.data.mapper.toDomain
import com.example.playlistmaker.data.network.ITunesApiService
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.repository.SearchRepository

class SearchRepositoryImpl(
    private val iTunesApiService: ITunesApiService
) : SearchRepository {

    override suspend fun searchTracks(query: String): List<Track> {
        val response = iTunesApiService.search(query)
        return response.results.orEmpty().map { it.toDomain() }
    }
}