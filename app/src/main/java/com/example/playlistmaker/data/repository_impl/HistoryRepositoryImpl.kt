package com.example.playlistmaker.data.repository_impl

import com.example.playlistmaker.data.mapper.toDomain
import com.example.playlistmaker.data.mapper.toDto
import com.example.playlistmaker.data.prefs.PreferencesDataSource
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.repository.HistoryRepository

class HistoryRepositoryImpl(
    private val prefs: PreferencesDataSource
) : HistoryRepository {

    override fun getHistory(): List<Track> {
        return prefs.getHistory().map { it.toDomain() }
    }

    override fun saveHistory(history: List<Track>) {
        prefs.saveHistory(history.map { it.toDto() })
    }

    override fun clearHistory() {
        prefs.saveHistory(emptyList())
    }
}