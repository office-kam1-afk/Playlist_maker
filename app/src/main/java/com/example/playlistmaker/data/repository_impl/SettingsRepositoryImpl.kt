package com.example.playlistmaker.data.repository_impl

import com.example.playlistmaker.data.prefs.PreferencesDataSource
import com.example.playlistmaker.domain.repository.SettingsRepository
class SettingsRepositoryImpl(
    private val prefs: PreferencesDataSource
) : SettingsRepository {
    override fun getTheme(): Boolean {
        return prefs.getTheme()
    }

        override fun setTheme(isDark: Boolean) {
            prefs.setTheme(isDark)
        }
}