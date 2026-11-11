package com.example.playlistmaker.data.repository_impl

import com.example.playlistmaker.data.db.FavoritesDao
import com.example.playlistmaker.data.mapper.toDomain
import com.example.playlistmaker.data.network.ITunesApiService
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(
    private val iTunesApiService: ITunesApiService,
    private val favoritesDao: FavoritesDao
    ) : SearchRepository {

    override fun searchTracks(query: String): Flow<List<Track>> = flow {
        val response = iTunesApiService.search(query)
        val tracks = response.results.orEmpty().map { it.toDomain() }

        val favoriteIds = favoritesDao.getAllIds()
        tracks.forEach { track ->
            track.isFavorite = favoriteIds.contains(track.trackId)
        }

        emit(tracks)
    }
}