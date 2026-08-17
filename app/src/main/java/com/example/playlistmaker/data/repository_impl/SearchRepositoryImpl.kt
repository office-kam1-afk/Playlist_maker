package com.example.playlistmaker.data.repository_impl

import com.example.playlistmaker.data.mapper.toDomain
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.repository.SearchRepository

class SearchRepositoryImpl : SearchRepository {
    override suspend fun searchTracks(query: String) =
        RetrofitNetworkClient.api.search(query).results.map { it.toDomain() }
}