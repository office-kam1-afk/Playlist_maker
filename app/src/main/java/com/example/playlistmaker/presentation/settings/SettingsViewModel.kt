package com.example.playlistmaker.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.interactor.SettingsInteractor

data class SettingsScreenState(val isDarkTheme: Boolean = false)

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val _screenState = MutableLiveData<SettingsScreenState>()
    val screenState: LiveData<SettingsScreenState> = _screenState

    init {
        _screenState.value = SettingsScreenState(isDarkTheme = settingsInteractor.getTheme())
    }

    fun onThemeSwitched(isDark: Boolean) {
        settingsInteractor.setTheme(isDark)
        _screenState.value = SettingsScreenState(isDarkTheme = isDark)
    }
}