package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.entity.Track

interface HistoryRepository {
    suspend fun getHistory(): List<Track>
    suspend fun saveHistory(history: List<Track>)
    suspend fun clearHistory()
}