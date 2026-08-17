package com.example.playlistmaker.data.network

import com.example.playlistmaker.domain.entity.Track

data class SearchResponse(
  val resultCount: Int,
val results: List<Track> = emptyList()
)