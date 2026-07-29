package com.example.playlistmaker

import android.app.appsearch.SearchResults

data class SearchResponse(
    val resultCount: Int,
    val results: List<Track> = emptyList()
)