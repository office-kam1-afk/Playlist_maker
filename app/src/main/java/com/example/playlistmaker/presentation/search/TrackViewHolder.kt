package com.example.playlistmaker.presentation.search

import android.view.View
import android.view.inputmethod.InputBinding
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemTrackBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.presentation.model.TrackUiModel


class TrackViewHolder(private val binding: ItemTrackBinding) :  RecyclerView.ViewHolder(binding.root) {
  // private val trackImage: ImageView = itemView.findViewById(R.id.track_image)
   // private val trackName: TextView = itemView.findViewById(R.id.track_name)
 //   private val artistName: TextView = itemView.findViewById(R.id.artist_name)

    fun bind(track: TrackUiModel) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName

        val totalSeconds = (track.trackTimeMillis ?: 0L) / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val durationString = String.format("%02d:%02d",minutes, seconds)

//val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
 // val duration = dateFormat.format(Date(track.trackTimeMillis))


        binding.trackDuration.text = durationString
       // artistName.text = "${track.artistName} • $durationString"

        //if (!track.artworkUrl100.isNullOrEmpty()) {
        Glide.with(binding.root.context)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .centerCrop()
            .apply(RequestOptions().transform(RoundedCorners(8)))
            .into(binding.trackImage)
        //} else {
           // trackImage.setImageResource(R.drawable.ic_placeholder)
        //}
    }
}