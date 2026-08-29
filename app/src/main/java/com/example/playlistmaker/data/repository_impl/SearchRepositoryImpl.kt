package com.example.playlistmaker.data.repository_impl

import com.example.playlistmaker.data.mapper.toDomain
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.repository.SearchRepository

class SearchRepositoryImpl : SearchRepository {
    override suspend fun searchTracks(query: String): List<Track> {
        val response = RetrofitNetworkClient.api.search(query)
        return response.results.orEmpty().map { it.toDomain() }
    }
}