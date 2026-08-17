package com.example.playlistmaker.domain.usecase
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.repository.SearchRepository
import retrofit2.http.Query

class SearchTracksUseCase (private val repository: SearchRepository) {
     suspend operator fun invoke(query: String): List<Track> {
          return repository.searchTracks(query)
     }
}