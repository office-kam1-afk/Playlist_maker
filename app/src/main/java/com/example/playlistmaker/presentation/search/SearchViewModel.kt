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
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val historyInteractor: HistoryInteractor
) : ViewModel() {

    private val _screenState = MutableLiveData<SearchScreenState>(SearchScreenState.Default)
    val screenState: LiveData<SearchScreenState> = _screenState

    private var searchJob: Job? = null

    fun onSearchQueryChanged(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            searchJob?.cancel()
            showHistory()
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500L) // Debounce
            _screenState.value = SearchScreenState.Loading
            try {
                val results = searchInteractor.searchTracks(trimmed)
                if (results.isEmpty()) _screenState.value = SearchScreenState.Empty
                else _screenState.value = SearchScreenState.Content(results)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _screenState.value = SearchScreenState.Error
            }
        }
    }

    fun showHistory() {
        val history = historyInteractor.getHistory()
        _screenState.value = if (history.isEmpty()) SearchScreenState.Default
        else SearchScreenState.History(history)
    }

    fun onTrackClicked(track: Track) = historyInteractor.addToHistory(track)
    fun onClearHistoryClicked() {
        historyInteractor.clearHistory()
        showHistory()
    }
    fun onRetryClicked(query: String) = onSearchQueryChanged(query)
}