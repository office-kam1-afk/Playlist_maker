package com.example.playlistmaker.presentation.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class PlayerFragment : Fragment() {

    private val viewModel: PlayerViewModel by viewModel()

    private lateinit var playPauseButton: ImageView
    private lateinit var progressText: TextView
    private lateinit var coverImage: ImageView
    private lateinit var trackNameText: TextView
    private lateinit var artistNameText: TextView
    private lateinit var durationValue: TextView
    private lateinit var albumLabel: TextView
    private lateinit var albumValue: TextView
    private lateinit var yearLabel: TextView
    private lateinit var yearValue: TextView
    private lateinit var genreLabel: TextView
    private lateinit var genreValue: TextView
    private lateinit var countryLabel: TextView
    private lateinit var countryValue: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        initToolbar(view)
        initListeners()
        observeViewModel()


        val track = getTrackFromArgs()
        viewModel.initTrack(track)
    }

    private fun initViews(view: View) {
        playPauseButton = view.findViewById(R.id.play_pause_button)
        progressText = view.findViewById(R.id.progress_text)
        coverImage = view.findViewById(R.id.cover_image)
        trackNameText = view.findViewById(R.id.track_name)
        artistNameText = view.findViewById(R.id.artist_name)
        durationValue = view.findViewById(R.id.duration_value)
        albumLabel = view.findViewById(R.id.album_label)
        albumValue = view.findViewById(R.id.album_name)
        yearLabel = view.findViewById(R.id.year_label)
        yearValue = view.findViewById(R.id.year_text)
        genreLabel = view.findViewById(R.id.genre_label)
        genreValue = view.findViewById(R.id.genre_text)
        countryLabel = view.findViewById(R.id.country_label)
        countryValue = view.findViewById(R.id.country_text)
    }

    private fun initToolbar(view: View) {
        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initListeners() {
        playPauseButton.setOnClickListener { viewModel.onPlayPauseClicked() }
        view?.findViewById<ImageView>(R.id.add_to_playlist)?.setOnClickListener { }
        view?.findViewById<ImageView>(R.id.favorite_button)?.setOnClickListener { }
    }

    private fun observeViewModel() {
        viewModel.screenState.observe(viewLifecycleOwner) { state ->
            state.errorMessage?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
            if (state.isTrackLoaded && state.track != null) renderTrackInfo(state.track)
            playPauseButton.setImageResource(if (state.isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
            progressText.text = state.progressText
        }
    }

    private fun renderTrackInfo(track: Track) {
        trackNameText.text = track.trackName
        artistNameText.text = track.artistName
        val totalSeconds = track.trackTimeMillis?.div(1000) ?: 0
        durationValue.text = String.format(Locale.getDefault(), "%02d:%02d", totalSeconds / 60, totalSeconds % 60)
        loadCover(track.artworkUrl100)
        setupOptionalField(albumLabel, albumValue, track.collectionName)
        setupYearField(yearLabel, yearValue, track.releaseDate)
        setupOptionalField(genreLabel, genreValue, track.primaryGenreName)
        setupOptionalField(countryLabel, countryValue, track.country)
    }

    private fun loadCover(artworkUrl: String?) {
        if (!artworkUrl.isNullOrEmpty()) {
            Glide.with(this).load(artworkUrl.replace("100x100bb.jpg", "512x512bb.jpg"))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder)
                .centerCrop().apply(RequestOptions().transform(RoundedCorners(8))).into(coverImage)
        } else coverImage.setImageResource(R.drawable.ic_placeholder)
    }

    private fun setupOptionalField(label: TextView, value: TextView, text: String?) {
        if (!text.isNullOrEmpty()) { value.text = text; label.visibility = View.VISIBLE; value.visibility = View.VISIBLE }
        else { label.visibility = View.GONE; value.visibility = View.GONE }
    }

    private fun setupYearField(label: TextView, value: TextView, releaseDate: String?) {
        if (!releaseDate.isNullOrEmpty() && releaseDate.length >= 4) {
            value.text = releaseDate.substring(0, 4); label.visibility = View.VISIBLE; value.visibility = View.VISIBLE
        } else { label.visibility = View.GONE; value.visibility = View.GONE }
    }

    private fun getTrackFromArgs(): Track {
        val args = requireArguments()
        return Track(
            trackId = args.getLong("trackId", 0L),
            trackName = args.getString("trackName"),
            artistName = args.getString("artistName"),
            trackTimeMillis = args.getLong("trackTimeMillis", 0L),
            artworkUrl100 = args.getString("artworkUrl100"),
            collectionName = args.getString("collectionName"),
            releaseDate = args.getString("releaseDate"),
            primaryGenreName = args.getString("primaryGenreName"),
            country = args.getString("country"),
            previewUrl = args.getString("previewUrl")
        )
    }

    override fun onPause() {
        super.onPause()
        viewModel.pauseForLifecycle()
    }
}