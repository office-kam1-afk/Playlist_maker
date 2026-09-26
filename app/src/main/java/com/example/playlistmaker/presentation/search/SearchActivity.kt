package com.example.playlistmaker.presentation.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.presentation.player.PlayerActivity
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    // Внедряем ViewModel через Koin
    private val viewModel: SearchViewModel by viewModel()

    private lateinit var historyAdapter: TrackAdapter
    private lateinit var searchAdapter: TrackAdapter
    private var searchJob: Job? = null

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var progressBar: View
    private lateinit var historyTitle: View
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: MaterialButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyPlaceholder: View
    private lateinit var errorPlaceholder: View
    private lateinit var retryButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        initToolbar()
        initAdapters()
        initListeners()
        observeViewModel()

        viewModel.showHistory()
    }

    override fun onResume() {
        super.onResume()
        if (searchEditText.text.toString().trim().isEmpty()) {
            viewModel.showHistory()
        }
    }

    private fun initViews() {
        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearButton)
        progressBar = findViewById(R.id.progressBar)
        historyTitle = findViewById(R.id.historyTitle)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        recyclerView = findViewById(R.id.recyclerView)
        emptyPlaceholder = findViewById(R.id.emptyPlaceholder)
        errorPlaceholder = findViewById(R.id.errorPlaceholder)
        retryButton = findViewById(R.id.retryButton)
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initAdapters() {
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyAdapter = TrackAdapter { track ->
            onTrackClicked(track)
        }
        historyRecyclerView.adapter = historyAdapter

        recyclerView.layoutManager = LinearLayoutManager(this)
        searchAdapter = TrackAdapter { track ->
            onTrackClicked(track)
        }
        recyclerView.adapter = searchAdapter
    }

    private fun initListeners() {
        searchEditText.doAfterTextChanged { editable ->
            val query = editable?.toString().orEmpty()
            clearButton.visibility = if (query.trim().isNotEmpty()) View.VISIBLE else View.GONE
            searchJob?.cancel()
            if (query.isEmpty()) {
                viewModel.showHistory()
            } else {
                searchJob = lifecycleScope.launch {
                    delay(SEARCH_DELAY_MS)
                    viewModel.onSearchQueryChanged(query)
                }
            }
        }

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            searchEditText.clearFocus()
            hideKeyboard()
        }

        clearHistoryButton.setOnClickListener {
            viewModel.onClearHistoryClicked()
        }

        retryButton.setOnClickListener {
            val query = searchEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    viewModel.onSearchQueryChanged(query)
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.screenState.observe(this) { state ->
            hideAllStates()
            when (state) {
                is SearchScreenState.Default -> {}
                is SearchScreenState.Loading -> progressBar.visibility = View.VISIBLE
                is SearchScreenState.Content -> {
                    recyclerView.visibility = View.VISIBLE
                    searchAdapter.submitList(state.tracks)
                }
                is SearchScreenState.Empty -> emptyPlaceholder.visibility = View.VISIBLE
                is SearchScreenState.Error -> errorPlaceholder.visibility = View.VISIBLE
                is SearchScreenState.History -> {
                    historyTitle.visibility = View.VISIBLE
                    historyRecyclerView.visibility = View.VISIBLE
                    clearHistoryButton.visibility = View.VISIBLE
                    historyAdapter.submitList(state.tracks)
                }
            }
        }
    }

    private fun hideAllStates() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        emptyPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE
        historyTitle.visibility = View.GONE
        historyRecyclerView.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
    }

    private fun onTrackClicked(track: Track) {
        viewModel.onTrackClicked(track)
        startActivity(PlayerActivity.createIntent(this, track))
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    companion object {
        private const val SEARCH_DELAY_MS = 500L
    }
}