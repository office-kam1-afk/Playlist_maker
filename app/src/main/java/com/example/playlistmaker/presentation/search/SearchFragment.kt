package com.example.playlistmaker.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Track
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        initAdapters()
        initListeners()
        observeViewModel()
        viewModel.showHistory()
}
    private fun initViews(view: View) {
    searchEditText = view.findViewById(R.id.searchEditText)
        clearButton = view.findViewById(R.id.clearButton)
        progressBar = view.findViewById(R.id.progressBar)
        historyTitle = view.findViewById(R.id.historyTitle)
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView)
        clearHistoryButton = view.findViewById(R.id.clearHistoryButton)
        recyclerView = view.findViewById(R.id.recyclerView)
        emptyPlaceholder = view.findViewById(R.id.emptyPlaceholder)
        errorPlaceholder = view.findViewById(R.id.errorPlaceholder)
        retryButton = view.findViewById(R.id.retryButton)
    }
    private fun initAdapters() {
        historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyAdapter = TrackAdapter { track -> onTrackClicked(track) }
        historyRecyclerView.adapter = historyAdapter

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchAdapter = TrackAdapter { track -> onTrackClicked(track) }
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
        viewModel.screenState.observe(viewLifecycleOwner) { state ->
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

        // Навигация к плееру через NavController с передачей данных трека
        val bundle = Bundle().apply {
            putLong("trackId", track.trackId ?: 0L)
            putString("trackName", track.trackName)
            putString("artistName", track.artistName)
            putLong("trackTimeMillis", track.trackTimeMillis ?: 0L)
            putString("artworkUrl100", track.artworkUrl100)
            putString("collectionName", track.collectionName)
            putString("releaseDate", track.releaseDate)
            putString("primaryGenreName", track.primaryGenreName)
            putString("country", track.country)
            putString("previewUrl", track.previewUrl)
        }
        findNavController().navigate(R.id.playerFragment, bundle)
    }
    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    override fun onResume() {
        super.onResume()
        if (searchEditText.text.toString().trim().isEmpty()) {
            viewModel.showHistory()
        }
    }

    companion object {
        private const val SEARCH_DELAY_MS = 500L
    }
}
