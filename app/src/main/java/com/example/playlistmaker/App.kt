package com.example.playlistmaker

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {


    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val isDarkThemeEnebled = sharedPreferences.getBoolean("dark_theme_enabled", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkThemeEnebled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

    }
}