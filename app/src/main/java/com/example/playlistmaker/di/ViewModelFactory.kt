package com.example.playlistmaker.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.domain.interactor.HistoryInteractor
import com.example.playlistmaker.domain.interactor.SearchInteractor
import com.example.playlistmaker.domain.interactor.SettingsInteractor
import com.example.playlistmaker.presentation.player.PlayerViewModel
import com.example.playlistmaker.presentation.search.SearchViewModel
import com.example.playlistmaker.presentation.settings.SettingsViewModel

// Фабрика умеет создавать ViewModel и передавать им нужные Interactor
class ViewModelFactory(
    private val searchInteractor: SearchInteractor,
    private val historyInteractor: HistoryInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SearchViewModel::class.java) ->
                SearchViewModel(searchInteractor, historyInteractor) as T
            modelClass.isAssignableFrom(PlayerViewModel::class.java) ->
                PlayerViewModel() as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(settingsInteractor) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}