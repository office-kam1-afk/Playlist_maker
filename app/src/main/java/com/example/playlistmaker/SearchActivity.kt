package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
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
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
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

    private var lastSearchQuery: String = ""


    private val searchHistory by lazy { (application as App).searchHistory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


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


        trackAdapter.onTrackClick = { track ->
            searchHistory.addTrack(track)
            updateHistoryUI()
        }


        historyAdapter.onTrackClick = { track ->
            searchHistory.addTrack(track)
            updateHistoryUI()
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


                if (query.isNotEmpty()) {
                    hideHistoryUI()
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
                    performSearch(query)
                    hideKeyboard()
                }
                true
            } else
                false
        }

        searchEditText.post {
            searchEditText.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)


            updateHistoryUI()
        }
    }

    private fun performSearch(query: String) {
        lastSearchQuery = query
        hideHistoryUI()
        showLoading()

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    NetworkClient.iTunesApiService.search(query)
                }
                withContext(Dispatchers.Main) {
                    if (response.resultCount > 0) {
                        showResults(response.results)
                    } else {
                        showEmptyState()
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) { showErrorState() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { showErrorState() }
            }
        }
    }

    private fun showLoading() {
        hideKeyboard()
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        emptyPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE
    }

    private fun showResults(tracks: List<Track>) {
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

                historyAdapter.updateTracks(history)


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
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        searchEditText.clearFocus()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}