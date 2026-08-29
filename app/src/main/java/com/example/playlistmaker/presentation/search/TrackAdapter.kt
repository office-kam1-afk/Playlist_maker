package com.example.playlistmaker.presentation.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Track
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class TrackAdapter(
    private val onTrackClick: (Track) -> Unit
) : ListAdapter<Track, TrackAdapter.TrackViewHolder>(TrackDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = getItem(position)

        holder.trackName.text = track.trackName
        holder.artistName.text = track.artistName
        holder.trackDuration.text = formatDuration(track.trackTimeMillis)

        Glide.with(holder.itemView.context)
            .load(track.artworkUrl100)
            .centerCrop()
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.trackImage)

        holder.itemView.setOnClickListener {
            onTrackClick(track)
        }
    }

    private fun formatDuration(millis: Long?): String {
        if (millis == null) return "00:00"

        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        return dateFormat.format(Date(millis))
    }

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trackImage: ImageView = itemView.findViewById(R.id.track_image)
        val trackName: TextView = itemView.findViewById(R.id.track_name)
        val artistName: TextView = itemView.findViewById(R.id.artist_name)
        val trackDuration: TextView = itemView.findViewById(R.id.track_duration)
    }
}

class TrackDiffCallback : DiffUtil.ItemCallback<Track>() {

    override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem.trackId == newItem.trackId
    }

    override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem == newItem
    }
}