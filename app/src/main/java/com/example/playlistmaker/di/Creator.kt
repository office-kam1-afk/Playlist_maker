package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.data.prefs.PreferencesDataSource
import com.example.playlistmaker.data.repository_impl.*
import com.example.playlistmaker.domain.repository.SearchRepository
import com.example.playlistmaker.domain.usecase.*

class Creator(context: Context) {
private val prefs = PreferencesDataSource(
    context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
)
    private val searchRepository = SearchRepositoryImpl()
    private val historyRepository = HistoryRepositoryImpl(prefs)
    private val settingsRepository = SettingsRepositoryImpl(prefs)

    val searchTracksUseCase = SearchTracksUseCase(searchRepository)
    val getHistoryUseCase = GetHistoryUseCase(historyRepository)
    val addToHistoryUseCase = AddToHistoryUseCase(historyRepository)
    val clearHistoryUseCase = ClearHistoryUseCase(historyRepository)
    val getThemeUseCase = GetThemeUseCase(settingsRepository)
    val setThemeUseCase = SetThemeUseCase(settingsRepository)
}