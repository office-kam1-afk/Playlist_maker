package com.example.playlistmaker.presentation.player

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Track
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)


        initViews()
        initToolbar()
        initListeners()
        observeViewModel()

        val track = intent.getTrackExtra()
        if (track.trackId == null || track.trackId == 0L) {
            Toast.makeText(this, "Ошибка: данные трека не получены", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        viewModel.initTrack(track)
    }

    private fun initViews() {
        playPauseButton = findViewById(R.id.play_pause_button)
        progressText = findViewById(R.id.progress_text)
        coverImage = findViewById(R.id.cover_image)
        trackNameText = findViewById(R.id.track_name)
        artistNameText = findViewById(R.id.artist_name)
        durationValue = findViewById(R.id.duration_value)
        albumLabel = findViewById(R.id.album_label)
        albumValue = findViewById(R.id.album_name)
        yearLabel = findViewById(R.id.year_label)
        yearValue = findViewById(R.id.year_text)
        genreLabel = findViewById(R.id.genre_label)
        genreValue = findViewById(R.id.genre_text)
        countryLabel = findViewById(R.id.country_label)
        countryValue = findViewById(R.id.country_text)
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initListeners() {
        playPauseButton.setOnClickListener { viewModel.onPlayPauseClicked() }
        findViewById<ImageView>(R.id.add_to_playlist).setOnClickListener { }
        findViewById<ImageView>(R.id.favorite_button).setOnClickListener { }
    }

    private fun observeViewModel() {
        viewModel.screenState.observe(this) { state ->
            state.errorMessage?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
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

    override fun onPause() {
        super.onPause()
        lifecycleScope.launch {
            viewModel.pauseForLifecycle()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        fun createIntent(context: Context, track: Track): Intent {
            return Intent(context, PlayerActivity::class.java).apply { putTrackExtra(track) }
        }
    }
}
fun Intent.putTrackExtra(track: Track) {
    putExtra("TRACK_ID", track.trackId ?: 0L)
    putExtra("TRACK_NAME", track.trackName)
    putExtra("ARTIST_NAME", track.artistName)
    putExtra("TRACK_TIME", track.trackTimeMillis ?: 0L)
    putExtra("ARTWORK_URL", track.artworkUrl100)
    putExtra("COLLECTION_NAME", track.collectionName)
    putExtra("RELEASE_DATE", track.releaseDate)
    putExtra("GENRE", track.primaryGenreName)
    putExtra("COUNTRY", track.country)
    putExtra("PREVIEW_URL", track.previewUrl)
}

fun Intent.getTrackExtra(): Track {
    return Track(
        trackId = getLongExtra("TRACK_ID", 0L),
        trackName = getStringExtra("TRACK_NAME"),
        artistName = getStringExtra("ARTIST_NAME"),
        trackTimeMillis = getLongExtra("TRACK_TIME", 0L),
        artworkUrl100 = getStringExtra("ARTWORK_URL"),
        collectionName = getStringExtra("COLLECTION_NAME"),
        releaseDate = getStringExtra("RELEASE_DATE"),
        primaryGenreName = getStringExtra("GENRE"),
        country = getStringExtra("COUNTRY"),
        previewUrl = getStringExtra("PREVIEW_URL")
    )
}