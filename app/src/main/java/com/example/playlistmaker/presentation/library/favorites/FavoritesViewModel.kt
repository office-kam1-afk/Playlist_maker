package com.example.playlistmaker.presentation.library.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.FavoritesInteractor
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _screenState = MutableLiveData<FavoritesScreenState>(FavoritesScreenState.Empty)
    val screenState: LiveData<FavoritesScreenState> = _screenState

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            favoritesInteractor.getFavorites().collect { tracks ->
                _screenState.value = if (tracks.isEmpty()) {
                    FavoritesScreenState.Empty
                } else {
                    FavoritesScreenState.Content(tracks)
                }
            }
        }
    }
}