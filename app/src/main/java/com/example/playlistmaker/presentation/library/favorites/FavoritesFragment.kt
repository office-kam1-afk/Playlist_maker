package com.example.playlistmaker.presentation.library.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.presentation.search.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private val viewModel: FavoritesViewModel by viewModel()
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyPlaceholder: View
    private lateinit var adapter: TrackAdapter

    companion object {
        fun newInstance(): FavoritesFragment = FavoritesFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        emptyPlaceholder = view.findViewById(R.id.emptyPlaceholder)

        adapter = TrackAdapter { track -> onTrackClicked(track) }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.screenState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoritesScreenState.Empty -> {
                    emptyPlaceholder.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
                is FavoritesScreenState.Content -> {
                    emptyPlaceholder.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    adapter.submitList(state.tracks)
                }
            }
        }
    }

    private fun onTrackClicked(track: Track) {
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
        findNavController().navigate(R.id.action_libraryFragment_to_playerFragment, bundle)
    }
}