package com.example.playlistmaker.domain.interactor

interface SettingsInteractor {
    fun getTheme(): Boolean
    fun setTheme(isDark: Boolean)
}