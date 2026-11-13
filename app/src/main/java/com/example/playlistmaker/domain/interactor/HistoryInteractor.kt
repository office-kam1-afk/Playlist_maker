package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.entity.Track

interface HistoryInteractor {
    suspend fun getHistory(): List<Track>
    suspend fun addToHistory(track: Track)
    suspend fun clearHistory()
}