package com.example.playlistmaker.domain.usecase
import com.example.playlistmaker.domain.repository.SettingsRepository

class SetThemeUseCase(private val repository: SettingsRepository) {
    operator fun invoke(isDark: Boolean) { repository.setTheme(isDark) }
}