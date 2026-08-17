package com.example.playlistmaker.domain.usecase
import com.example.playlistmaker.domain.repository.HistoryRepository

class ClearHistoryUseCase(private val repository: HistoryRepository) {
    operator fun invoke() { repository.clearHistory()}
}
