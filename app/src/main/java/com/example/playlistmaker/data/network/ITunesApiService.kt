package com.example.playlistmaker.data.network



import com.example.playlistmaker.data.dto.SearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiService {
    @GET("search")
    suspend fun search(
        @Query("term") query: String,
        @Query("entity") entity: String = "song"
    ): SearchResponseDto
}