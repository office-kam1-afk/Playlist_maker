package com.example.playlistmaker

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.prefs.PreferencesDataSource
import com.example.playlistmaker.data.repository_impl.HistoryRepositoryImpl
import com.example.playlistmaker.data.repository_impl.SearchRepositoryImpl
import com.example.playlistmaker.data.repository_impl.SettingsRepositoryImpl
import com.example.playlistmaker.di.ViewModelFactory
import com.example.playlistmaker.domain.interactor.HistoryInteractorImpl
import com.example.playlistmaker.domain.interactor.SearchInteractorImpl
import com.example.playlistmaker.domain.interactor.SettingsInteractorImpl

class App : Application() {

        lateinit var viewModelFactory: ViewModelFactory
        private set

    override fun onCreate() {
        super.onCreate()

       val prefs = PreferencesDataSource(
            getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        )

       val searchRepository = SearchRepositoryImpl()
        val historyRepository = HistoryRepositoryImpl(prefs)
        val settingsRepository = SettingsRepositoryImpl(prefs)

            val searchInteractor = SearchInteractorImpl(searchRepository)
        val historyInteractor = HistoryInteractorImpl(historyRepository)
        val settingsInteractor = SettingsInteractorImpl(settingsRepository)

          viewModelFactory = ViewModelFactory(searchInteractor, historyInteractor, settingsInteractor)


        val isDark = settingsInteractor.getTheme()
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}