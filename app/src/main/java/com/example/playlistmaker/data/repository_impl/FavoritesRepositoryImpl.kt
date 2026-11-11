package com.example.playlistmaker.data.repository_impl

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.toDomain
import com.example.playlistmaker.data.db.toEntity
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val database: AppDatabase
) : FavoritesRepository {

    private val dao = database.favoritesDao()

    override suspend fun addToFavorites(track: Track) {
        dao.insert(track.toEntity())
    }

    override suspend fun removeFromFavorites(track: Track) {
        dao.delete(track.toEntity())
    }

    override fun getFavorites(): Flow<List<Track>> {
        return dao.getAllFavorites().map { entities -> entities.map { it.toDomain() } }
    }
}