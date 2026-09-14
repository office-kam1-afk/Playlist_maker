package com.example.playlistmaker.presentation.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.presentation.player.PlayerActivity
import com.google.android.material.button.MaterialButton

class SearchActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var searchAdapter: TrackAdapter

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

        val factory = (application as App).viewModelFactory
        viewModel = ViewModelProvider(this, factory)[SearchViewModel::class.java]

        initViews()
        initToolbar()
        initAdapters()
        initListeners()
        observeViewModel()

        viewModel.showHistory()
    }

    override fun onResume() {
        super.onResume()
        if (searchEditText.text.toString().trim().isEmpty()) viewModel.showHistory()
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
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initAdapters() {
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyAdapter = TrackAdapter { onTrackClicked(it) }
        historyRecyclerView.adapter = historyAdapter

        recyclerView.layoutManager = LinearLayoutManager(this)
        searchAdapter = TrackAdapter { onTrackClicked(it) }
        recyclerView.adapter = searchAdapter
    }

    private fun initListeners() {
        searchEditText.doAfterTextChanged { editable ->
            val query = editable?.toString().orEmpty()
            clearButton.visibility = if (query.trim().isNotEmpty()) View.VISIBLE else View.GONE
            viewModel.onSearchQueryChanged(query)
        }
        clearButton.setOnClickListener {
            searchEditText.text.clear()
            searchEditText.clearFocus()
            hideKeyboard()
        }
        clearHistoryButton.setOnClickListener { viewModel.onClearHistoryClicked() }
        retryButton.setOnClickListener { viewModel.onRetryClicked(searchEditText.text.toString().trim()) }
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
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }
}