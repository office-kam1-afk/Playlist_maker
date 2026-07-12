package com.example.playlistmaker


import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.os.PersistableBundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.concurrent.TimeUnit
import com.bumptech.glide.request.RequestOptions


class PlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        val track = intent.getSerializableExtra("track") as? Track

        if (track == null) {
            finish()
            return
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { finish() }


        val coverImage = findViewById<ImageView>(R.id.cover_image)
        val trackNameText = findViewById<TextView>(R.id.track_name)
        val artistNameText = findViewById<TextView>(R.id.artist_name)
        val albumNameText = findViewById<TextView>(R.id.album_name)


        val albumValue = findViewById<TextView>(R.id.album_name)
        val albumLabel = findViewById<TextView>(R.id.album_label)
        val yearText = findViewById<TextView>(R.id.year_text)
        val yearLabel = findViewById<TextView>(R.id.year_label)
        val yearValue = findViewById<TextView>(R.id.year_text)
        val genreText = findViewById<TextView>(R.id.genre_text)
        val genreValue = findViewById<TextView>(R.id.genre_text)
        val genreLabel = findViewById<TextView>(R.id.genre_label)
        val countryText = findViewById<TextView>(R.id.country_text)
        val countryValue = findViewById<TextView>(R.id.country_text)
        val countryLabel = findViewById<TextView>(R.id.country_label)
        val durationValue = findViewById<TextView>(R.id.duration_value)
        val addToPlaylistButton = findViewById<ImageView>(R.id.add_to_playlist)
        val favoriteButton = findViewById<ImageView>(R.id.favorite_button)
        val playPauseButton = findViewById<ImageView>(R.id.play_pause_button)
        val progressText = findViewById<TextView>(R.id.progress_text)

        trackNameText.text = track.trackName
        artistNameText.text = track.artistName

        val totalSeconds = track.trackTimeMillis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val durationString = String.format("%02d:%02d", minutes, seconds)
        durationValue.text = durationString
        progressText.text = "0:00"

        if (!track.artworkUrl100.isNullOrEmpty()) {
            val highQualityUrl = track.artworkUrl100.replace("100x100bb.jpg", "512x512bb.jpg")
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
        if (!track.collectionName.isNullOrEmpty()) {

            albumValue.text = track.collectionName
            albumLabel.visibility = View.VISIBLE
            albumValue.visibility = View.VISIBLE
        } else {
            albumLabel.visibility = View.GONE
            albumValue.visibility = View.GONE
        }

        if (!track.releaseDate.isNullOrEmpty() && track.releaseDate.length >= 4) {
            val year = track.releaseDate.substring(0, 4)
            yearValue.text = year
            yearLabel.visibility = View.VISIBLE
            yearValue.visibility = View.VISIBLE
        } else {
            yearLabel.visibility = View.GONE
            yearValue.visibility = View.GONE
        }
        if (!track.primaryGenreName.isNullOrEmpty()) {
            genreValue.text = track.primaryGenreName

            genreLabel.visibility = View.VISIBLE
            genreValue.visibility = View.VISIBLE
        } else {
            genreLabel.visibility = View.GONE
            genreValue.visibility = View.GONE
        }

        if (!track.country.isNullOrEmpty()) {
            countryValue.text = track.country
            countryLabel.visibility = View.VISIBLE
            countryValue.visibility = View.VISIBLE
        } else {
            countryLabel.visibility = View.GONE
            countryValue.visibility = View.GONE
        }
        findViewById<ImageView>(R.id.add_to_playlist).setOnClickListener { }
        findViewById<ImageView>(R.id.favorite_button).setOnClickListener { }
        findViewById<ImageView>(R.id.play_pause_button).setOnClickListener { }

    }

        override fun onSupportNavigateUp(): Boolean {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
    }

