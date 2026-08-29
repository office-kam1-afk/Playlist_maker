package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.repository.SettingsRepository

class SettingsInteractorImpl(
    private val repository: SettingsRepository) : SettingsInteractor {
    override fun getTheme(): Boolean {
        return repository.getTheme()
    }

    override fun setTheme(isDark: Boolean) {
        repository.setTheme(isDark)
    }
    }

