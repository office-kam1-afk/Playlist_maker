package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.repository.HistoryRepository

class HistoryInteractorImpl (
    private val repository: HistoryRepository
) : HistoryInteractor {
    private val maxHistorySize = 10
    override fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun addToHistory(track: Track) {
        val history = repository.getHistory().toMutableList()
        track.trackId?.let {trackId ->
            history.removeAll {it.trackId == trackId }
        }
    history.add(0, track)
        repository.saveHistory(history.take(maxHistorySize))
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}