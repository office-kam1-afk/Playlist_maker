package com.example.playlistmaker.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.interactor.HistoryInteractor
import com.example.playlistmaker.domain.interactor.SearchInteractor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val historyInteractor: HistoryInteractor
) : ViewModel() {

    private val _screenState = MutableLiveData<SearchScreenState>(SearchScreenState.Default)
    val screenState: LiveData<SearchScreenState> = _screenState

    private var searchJob: Job? = null
    private var clickJob: Job? = null

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 500L
        private const val CLICK_DEBOUNCE_MS = 1000L
    }

    fun onSearchQueryChanged(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            searchJob?.cancel()
            showHistory()
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS) // Debounce для поиска
            _screenState.value = SearchScreenState.Loading
            try {
                searchInteractor.searchTracks(trimmed)
                    .catch { _screenState.value = SearchScreenState.Error }
                    .collect { tracks ->
                        if (tracks.isEmpty()) _screenState.value = SearchScreenState.Empty
                        else _screenState.value = SearchScreenState.Content(tracks)
                    }
            } catch (e: CancellationException) {
                throw e
            }
        }
    }

    fun showHistory() {
        val history = historyInteractor.getHistory()
        _screenState.value = if (history.isEmpty()) SearchScreenState.Default
        else SearchScreenState.History(history)
    }

        fun onTrackClicked(track: Track): Boolean {
                if (clickJob?.isActive == true) return false
        historyInteractor.addToHistory(track)
        clickJob = viewModelScope.launch {
            delay(CLICK_DEBOUNCE_MS)
            clickJob = null
        }
        return true
    }

    fun onClearHistoryClicked() {
        historyInteractor.clearHistory()
        showHistory()
    }
}