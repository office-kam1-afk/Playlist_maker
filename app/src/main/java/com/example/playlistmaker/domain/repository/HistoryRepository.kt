package com.example.playlistmaker.domain.repository
import com.example.playlistmaker.domain.entity.Track
interface HistoryRepository{
    fun getHistory(): List<Track>
    fun addToHistory(track: Track)
    fun clearHistory()
}