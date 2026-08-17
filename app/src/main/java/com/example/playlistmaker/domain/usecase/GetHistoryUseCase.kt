package com.example.playlistmaker.domain.usecase
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.repository.HistoryRepository

class GetHistoryUseCase  (private val repository: HistoryRepository) {
    operator fun invoke(): List<Track> { return repository.getHistory()}
    }