package com.example.playlistmaker.presentation.search

import com.example.playlistmaker.domain.entity.Track

sealed class SearchScreenState{
    object Default : SearchScreenState()
    object Loading : SearchScreenState()
    data class Content(val tracks: List<Track>) : SearchScreenState()
    object Empty : SearchScreenState()
    object Error : SearchScreenState()
    data class History(val tracks: List<Track>) : SearchScreenState()
}