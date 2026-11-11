package com.example.playlistmaker.data.repository_impl

import com.example.playlistmaker.data.db.FavoritesDao
import com.example.playlistmaker.data.mapper.toDomain
import com.example.playlistmaker.data.mapper.toDto
import com.example.playlistmaker.data.prefs.PreferencesDataSource
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.repository.HistoryRepository

class HistoryRepositoryImpl(
    private val prefs: PreferencesDataSource,
    private val favoritesDao: FavoritesDao
) : HistoryRepository {

    override suspend fun getHistory(): List<Track> {
        val history = prefs.getHistory().map { it.toDomain() }
        val favoriteIds = favoritesDao.getAllIds()

        history.forEach { track ->
            track.isFavorite = favoriteIds.contains(track.trackId)
        }
        return history
    }
    override suspend fun saveHistory(history: List<Track>) = prefs.saveHistory(history.map { it.toDto() })
    override suspend fun clearHistory() = prefs.saveHistory(emptyList())
}