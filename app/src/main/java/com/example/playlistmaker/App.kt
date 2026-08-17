package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.Creator
class App : Application() {
lateinit var creator: Creator

    override fun onCreate() {
        super.onCreate()
       creator = Creator(this)

val isDark = creator.getThemeUseCase()
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

    }
}