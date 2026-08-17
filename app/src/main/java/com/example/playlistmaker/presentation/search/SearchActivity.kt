package com.example.playlistmaker.presentation.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.presentation.search.TrackAdapter
import com.example.playlistmaker.presentation.model.TrackUiModel
import com.example.playlistmaker.presentation.model.toUiModel
import com.example.playlistmaker.presentation.player.PlayerActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.SearchHistory
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.entity.Track
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class SearchActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: View
    private lateinit var emptyPlaceholder: LinearLayout
    private lateinit var errorPlaceholder: LinearLayout
    private lateinit var retryButton: MaterialButton
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyTitle: TextView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: MaterialButton
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var searchHistory: SearchHistory
    private var lastSearchQuery: String = ""

    // Для debounce поиска
    private var searchJob: Job? = null
    private val handler = Handler(Looper.getMainLooper())
    private var debounceRunnable: Runnable? = null

    // Для debounce кликов
    private var lastClickTime = 0L
    private val minClickInterval = 500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val sharedPreferences = getSharedPreferences("app_settings", MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
        }
        toolbar.setNavigationOnClickListener { finish() }

        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearButton)
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        emptyPlaceholder = findViewById(R.id.emptyPlaceholder)
        errorPlaceholder = findViewById(R.id.errorPlaceholder)
        retryButton = findViewById(R.id.retryButton)
        historyTitle = findViewById(R.id.historyTitle)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)

        recyclerView.layoutManager = LinearLayoutManager(this)
        trackAdapter = TrackAdapter(emptyList())
        recyclerView.adapter = trackAdapter

        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyAdapter = TrackAdapter(emptyList())
        historyRecyclerView.adapter = historyAdapter

        // ИСПРАВЛЕНО: параметр лямбды теперь 'uiTrack'
        trackAdapter.onTrackClick = { uiTrack ->
            if (isClickValid()) {
                val domainTrack = Track(
                    trackId = uiTrack.trackId,
                    trackName = uiTrack.trackName,
                    artistName = uiTrack.artistName,
                    trackTimeMillis = uiTrack.trackTimeMillis,
                    artworkUrl100 = uiTrack.artworkUrl100,
                    collectionName = uiTrack.collectionName,
                    releaseDate = uiTrack.releaseDate,
                    primaryGenreName = uiTrack.primaryGenreName,
                    country = uiTrack.country,
                    previewUrl = uiTrack.previewUrl
                )

                searchHistory.addTrack(domainTrack)
                updateHistoryUI()
                val intent = Intent(this@SearchActivity, PlayerActivity::class.java).apply {
                    putExtra("track", domainTrack)
                }
                startActivity(intent)
            }
        }

        historyAdapter.onTrackClick = { uiTrack ->
            if (isClickValid()) {
                val domainTrack = Track(
                    trackId = uiTrack.trackId,
                    trackName = uiTrack.trackName,
                    artistName = uiTrack.artistName,
                    trackTimeMillis = uiTrack.trackTimeMillis,
                    artworkUrl100 = uiTrack.artworkUrl100,
                    collectionName = uiTrack.collectionName,
                    releaseDate = uiTrack.releaseDate,
                    primaryGenreName = uiTrack.primaryGenreName,
                    country = uiTrack.country,
                    previewUrl = uiTrack.previewUrl
                )

                searchHistory.addTrack(domainTrack)
                updateHistoryUI()

                val intent = Intent(this@SearchActivity, PlayerActivity::class.java).apply {
                    putExtra("track", domainTrack)
                }
                startActivity(intent)
            }
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            updateHistoryUI()
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim() ?: ""
                clearButton.visibility = if (query.isEmpty()) View.GONE else View.VISIBLE

                debounceRunnable?.let { handler.removeCallbacks(it) }

                if (query.isNotEmpty()) {
                    hideHistoryUI()
                    debounceRunnable = Runnable {
                        performSearch(query)
                    }
                    handler.postDelayed(debounceRunnable!!, 2000)
                } else {
                    updateHistoryUI()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        clearButton.setOnClickListener {
            clearSearch()
        }

        retryButton.setOnClickListener {
            if (lastSearchQuery.isNotEmpty()) performSearch(lastSearchQuery)
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    debounceRunnable?.let { handler.removeCallbacks(it) }
                    performSearch(query)
                    hideKeyboard()
                }
                true
            } else false
        }

        searchEditText.post {
            searchEditText.requestFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
            updateHistoryUI()
        }
    }

    private fun isClickValid(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < minClickInterval) {
            return false
        }
        lastClickTime = currentTime
        return true
    }

    private fun performSearch(query: String) {
        lastSearchQuery = query
        searchJob?.cancel()
        hideHistoryUI()
        showLoading()

        searchJob = lifecycleScope.launch {
            try {
                // ИСПРАВЛЕНО: добавлены 's' в конце имен переменных (domainTracks, uiModels)
                val domainTracks: List<Track> = withContext(Dispatchers.IO) {
                    //searchTracksUseCase(query)
                    //emptyList()
                    try {
                        val response = RetrofitNetworkClient.api.search(query)
                        response.results.map { dto ->
                            Track(
                                trackId = dto.trackId,
                                trackName = dto.trackName,
                                artistName = dto.artistName,
                                trackTimeMillis = dto.trackTimeMillis,
                                artworkUrl100 = dto.artworkUrl100,
                                collectionName = dto.collectionName,
                                releaseDate = dto.releaseDate,
                                primaryGenreName = dto.primaryGenreName,
                                country = dto.country,
                                previewUrl = dto.previewUrl
                            )
                        }
                    } catch (e: Exception) {
                        emptyList()
                    }
                }
                 val uiModels: List<TrackUiModel> = domainTracks.map { it.toUiModel() }

                withContext(Dispatchers.Main) {
                    if (uiModels.isEmpty()) {
                        showEmptyState()
                    } else {
                        showResults(uiModels)
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                withContext(Dispatchers.Main) { showErrorState() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { showErrorState() }
            }
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        emptyPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE
    }

    private fun showResults(tracks: List<TrackUiModel>) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        emptyPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE
        trackAdapter.updateTracks(tracks)
    }

    private fun showEmptyState() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        emptyPlaceholder.visibility = View.VISIBLE
        errorPlaceholder.visibility = View.GONE
    }

    private fun showErrorState() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        emptyPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.VISIBLE
    }

    private fun clearSearch() {
        searchEditText.setText("")
        lastSearchQuery = ""
        clearButton.visibility = View.GONE
        debounceRunnable?.let { handler.removeCallbacks(it) }
        hideAllViews()
        hideKeyboard()
        updateHistoryUI()
    }

    private fun hideAllViews() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        emptyPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE
    }

    private fun updateHistoryUI() {
        val query = searchEditText.text.toString().trim()
        if (query.isEmpty()) {
            val history = searchHistory.getHistory()
            if (history.isNotEmpty()) {
                historyTitle.visibility = View.VISIBLE
                historyRecyclerView.visibility = View.VISIBLE
                clearHistoryButton.visibility = View.VISIBLE
                val historyUiModels = history.map {it.toUiModel()}
                historyAdapter.updateTracks(historyUiModels)
                hideAllViews()
            } else {
                hideHistoryUI()
                hideAllViews()
            }
        } else {
            hideHistoryUI()
        }
    }

    private fun hideHistoryUI() {
        historyTitle.visibility = View.GONE
        historyRecyclerView.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        searchEditText.clearFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        searchJob?.cancel()
        debounceRunnable?.let { handler.removeCallbacks(it) }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}