package com.example.playlistmaker.data.mapper

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.entity.Track
fun TrackDto.toDomain(): Track = Track(
    trackName, trackId, artistName, trackTimeMillis, artworkUrl100,
    collectionName, releaseDate, primaryGenreName, country, previewUrl
)

fun Track.toDto(): TrackDto = TrackDto(
    trackName, trackId, artistName, trackTimeMillis, artworkUrl100,
    collectionName, releaseDate, primaryGenreName, country, previewUrl
)