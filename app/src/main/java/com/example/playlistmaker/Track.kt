package com.example.playlistmaker

data class Track(
    val trackName: String,
    val trackId: Long,
    val artistName: String,
    val trackTimeMillis: Long,
    val coverUrl: String? = null, // Знак ? и = null обязательны для Gson!
    val duration: Int? = null,
    val artworkUrl100: String?
)

