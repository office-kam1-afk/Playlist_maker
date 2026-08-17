package com.example.playlistmaker.domain.usecase
import com.example.playlistmaker.domain.repository.SettingsRepository

class GetThemeUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): Boolean { return repository.getTheme()}
}