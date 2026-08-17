package com.example.playlistmaker.data.repository_impl

import com.example.playlistmaker.data.mapper.toDto
import com.example.playlistmaker.data.mapper.toDomain
import com.example.playlistmaker.data.prefs.PreferencesDataSource
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.repository.HistoryRepository

class HistoryRepositoryImpl(private val prefs: PreferencesDataSource) : HistoryRepository {
    private val maxSize = 10


    override fun getHistory() = prefs.getHistory().map { it.toDomain() }
    override fun addToHistory(track: Track) {
        val history = prefs.getHistory().toMutableList()
        val dto = track.toDto()
        // Удаляем дубликат, если он есть
        history.removeAll { it.trackId == dto.trackId }
        history.add(0, dto)
        if (history.size > maxSize) history.removeAt(maxSize)
        prefs.saveHistory(history)
    }

    override fun clearHistory() = prefs.saveHistory(emptyList())
}