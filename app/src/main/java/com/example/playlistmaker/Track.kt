package com.example.playlistmaker

import java.io.Serializable

data class Track(
    val trackName: String,
    val trackId: Long,
    val artistName: String,
    val trackTimeMillis: Long? = 0L,
    val artworkUrl100: String?,
    val collectionName: String? = null,
    val releaseDate: String? = null,
    val primaryGenreName: String? = null,
    val country: String? = null,
    val previewUrl: String?
) : Serializable