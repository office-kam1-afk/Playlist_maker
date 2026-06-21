package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackImage: ImageView = itemView.findViewById(R.id.track_image)
    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val artistName: TextView = itemView.findViewById(R.id.artist_name)

    fun bind(track: Track) {
        trackName.text = track.trackName

        val totalSeconds = track.trackTimeMillis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val durationString = String.format("%02d:%02d",minutes, seconds)

        artistName.text = "${track.artistName} • $durationString"

        if (!track.artworkUrl100.isNullOrEmpty()) {
            Glide.with(itemView.context)
                .load(track.artworkUrl100)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .centerCrop()
                .apply(RequestOptions().transform(RoundedCorners(8)))
                .into(trackImage)
        } else {
            trackImage.setImageResource(R.drawable.ic_placeholder)
        }
    }
}