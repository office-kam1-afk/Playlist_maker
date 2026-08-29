package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.data.prefs.PreferencesDataSource
import com.example.playlistmaker.data.repository_impl.HistoryRepositoryImpl
import com.example.playlistmaker.data.repository_impl.SearchRepositoryImpl
import com.example.playlistmaker.data.repository_impl.SettingsRepositoryImpl
import com.example.playlistmaker.domain.interactor.HistoryInteractor
import com.example.playlistmaker.domain.interactor.HistoryInteractorImpl
import com.example.playlistmaker.domain.interactor.SearchInteractor
import com.example.playlistmaker.domain.interactor.SearchInteractorImpl
import com.example.playlistmaker.domain.interactor.SettingsInteractor
import com.example.playlistmaker.domain.interactor.SettingsInteractorImpl
import com.example.playlistmaker.domain.repository.HistoryRepository
import com.example.playlistmaker.domain.repository.SearchRepository
import com.example.playlistmaker.domain.repository.SettingsRepository

class Creator(context: Context) {
    private val PreferencesDataSource = PreferencesDataSource(
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    )
    private val searchRepository: SearchRepository = SearchRepositoryImpl()
    private val historyRepository: HistoryRepository = HistoryRepositoryImpl(PreferencesDataSource)
    private val settingsRepository: SettingsRepository =
        SettingsRepositoryImpl(PreferencesDataSource)

    val searchInteractor: SearchInteractor = SearchInteractorImpl(searchRepository)
    val historyInteractor: HistoryInteractor = HistoryInteractorImpl(historyRepository)
    val settingsInteractor: SettingsInteractor = SettingsInteractorImpl(settingsRepository)
}
