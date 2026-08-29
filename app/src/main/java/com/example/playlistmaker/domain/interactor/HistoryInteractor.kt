package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.entity.Track
 interface HistoryInteractor {
     fun getHistory(): List<Track>
     fun addToHistory(track: Track)
     fun clearHistory()
 }