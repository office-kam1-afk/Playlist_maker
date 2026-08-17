package com.example.playlistmaker.domain.repository

interface SettingsRepository {
    fun getTheme(): Boolean
    fun setTheme(isDark: Boolean )
 }