package com.example.playlistmaker.presentation.player

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Track
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

       private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var isPrepared = false
    private var currentPosition = 0

    private val handler = Handler(Looper.getMainLooper())


    private val progressRunnable = object : Runnable {
        override fun run() {
            val mp = mediaPlayer ?: return

            if (isPlaying && isPrepared) {
                val currentPos = mp.currentPosition
                val totalMinutes = currentPos / 60000
                val totalSeconds = (currentPos % 60000) / 1000

                val progressString = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    totalMinutes,
                    totalSeconds
                )

                progressText.text = progressString


                handler.postDelayed(this, 500L)
            }
        }
    }

    private lateinit var playPauseButton: ImageView
    private lateinit var progressText: TextView

    private var track: Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        playPauseButton = findViewById(R.id.play_pause_button)
        progressText = findViewById(R.id.progress_text)

        @Suppress("DEPRECATION")
        track = intent.getParcelableExtra("track") as? Track

        if (track == null) {
            Toast.makeText(this, "Ошибка: данные трека не получены", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initToolbar()

        val coverImage = findViewById<ImageView>(R.id.cover_image)
        val trackNameText = findViewById<TextView>(R.id.track_name)
        val artistNameText = findViewById<TextView>(R.id.artist_name)

        val albumValue = findViewById<TextView>(R.id.album_name)
        val albumLabel = findViewById<TextView>(R.id.album_label)

        val yearValue = findViewById<TextView>(R.id.year_text)
        val yearLabel = findViewById<TextView>(R.id.year_label)

        val genreValue = findViewById<TextView>(R.id.genre_text)
        val genreLabel = findViewById<TextView>(R.id.genre_label)

        val countryValue = findViewById<TextView>(R.id.country_text)
        val countryLabel = findViewById<TextView>(R.id.country_label)

        val durationValue = findViewById<TextView>(R.id.duration_value)

        findViewById<ImageView>(R.id.add_to_playlist).setOnClickListener { }
        findViewById<ImageView>(R.id.favorite_button).setOnClickListener { }

        trackNameText.text = track?.trackName
        artistNameText.text = track?.artistName

        val totalSeconds = track?.trackTimeMillis?.div(1000) ?: 0
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val durationString = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

        durationValue.text = durationString
        progressText.text = "00:00"

        loadCover(coverImage)

        setupOptionalField(albumLabel, albumValue, track?.collectionName)
        setupYearField(yearLabel, yearValue, track?.releaseDate)
        setupOptionalField(genreLabel, genreValue, track?.primaryGenreName)
        setupOptionalField(countryLabel, countryValue, track?.country)

        playPauseButton.setOnClickListener {
            if (isPlaying) {
                pausePlayback()
            } else {
                startPlayback()
            }
        }
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toolbar.setNavigationOnClickListener {
            stopPlayback()
            finish()
        }
    }

    private fun loadCover(coverImage: ImageView) {
        val artworkUrl = track?.artworkUrl100

        if (!artworkUrl.isNullOrEmpty()) {
            val highQualityUrl = artworkUrl.replace("100x100bb.jpg", "512x512bb.jpg")

            Glide.with(this)
                .load(highQualityUrl)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .centerCrop()
                .apply(RequestOptions().transform(RoundedCorners(8)))
                .into(coverImage)
        } else {
            coverImage.setImageResource(R.drawable.ic_placeholder)
        }
    }

    private fun setupOptionalField(label: TextView, value: TextView, text: String?) {
        if (!text.isNullOrEmpty()) {
            value.text = text
            label.visibility = View.VISIBLE
            value.visibility = View.VISIBLE
        } else {
            label.visibility = View.GONE
            value.visibility = View.GONE
        }
    }

    private fun setupYearField(label: TextView, value: TextView, releaseDate: String?) {
        if (!releaseDate.isNullOrEmpty() && releaseDate.length >= 4) {
            val year = releaseDate.substring(0, 4)
            value.text = year
            label.visibility = View.VISIBLE
            value.visibility = View.VISIBLE
        } else {
            label.visibility = View.GONE
            value.visibility = View.GONE
        }
    }

    private fun startPlayback() {
        val url = track?.previewUrl

        if (url.isNullOrEmpty()) {
            Toast.makeText(this, "Отрывок недоступен", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(url)

                    setOnPreparedListener { mp ->
                        isPrepared = true

                        if (!isFinishing && !isDestroyed) {
                            if (currentPosition > 0) {
                                mp.seekTo(currentPosition)
                            }
                            mp.start()

                            this@PlayerActivity.isPlaying = true

                            updatePlayPauseButton()
                            startProgressUpdates()
                        }
                    }

                    setOnCompletionListener {
                        stopPlayback()
                    }

                    setOnErrorListener { _, what, extra ->
                        Log.e("PlayerActivity", "MediaPlayer Error: what=$what, extra=$extra")
                        Toast.makeText(this@PlayerActivity, "Ошибка воспроизведения", Toast.LENGTH_SHORT).show()
                        stopPlayback()
                        true
                    }

                    prepareAsync()
                }
            } else {
                if (!isPrepared) {
                    return
                }

                mediaPlayer?.start()
                isPlaying = true
                updatePlayPauseButton()
                startProgressUpdates()
            }
        } catch (e: Exception) {
            Log.e("PlayerActivity", "Exception in startPlayback", e)
            Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            stopPlayback()
        }
    }

    private fun pausePlayback() {
        mediaPlayer?.let { mp ->
            if (!isPrepared) {
                stopPlayback()
                return
            }

            if (mp.isPlaying) {
                currentPosition = mp.currentPosition
                mp.pause()
            }
        }

        isPlaying = false
        updatePlayPauseButton()
        stopProgressUpdates()
    }

    private fun stopPlayback() {
        stopProgressUpdates()

        isPlaying = false
        isPrepared = false
        currentPosition = 0

        mediaPlayer?.let { mp ->
            runCatching {
                if (mp.isPlaying) {
                    mp.stop()
                }
            }
            runCatching {
                mp.reset()
            }
            runCatching {
                mp.release()
            }
        }

        mediaPlayer = null
        updatePlayPauseButton()
        progressText.text = "00:00"
    }

    private fun updatePlayPauseButton() {
        if (isPlaying) {
            playPauseButton.setImageResource(R.drawable.ic_pause)
        } else {
            playPauseButton.setImageResource(R.drawable.ic_play)
        }
    }

    private fun startProgressUpdates() {
        handler.removeCallbacks(progressRunnable)
        handler.post(progressRunnable)
    }

    private fun stopProgressUpdates() {
        handler.removeCallbacks(progressRunnable)
    }

    override fun onPause() {
        super.onPause()
        if (isPlaying) {
            pausePlayback()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlayback()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}