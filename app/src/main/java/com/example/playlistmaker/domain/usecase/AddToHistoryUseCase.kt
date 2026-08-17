package com.example.playlistmaker.domain.usecase
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.repository.HistoryRepository
class AddToHistoryUseCase(private val repository: HistoryRepository) {
    operator fun invoke(track: Track) {
        repository.addToHistory(track)
    }
}