package com.example.playlistmaker.presentation.search


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.search.TrackViewHolder
import com.example.playlistmaker.databinding.ItemTrackBinding
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.presentation.model.TrackUiModel

class TrackAdapter(private var tracks: List<TrackUiModel>) : RecyclerView.Adapter<TrackViewHolder>() {
    var onTrackClick: ((TrackUiModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
           onTrackClick?.invoke(track)
        }
    }
    override fun getItemCount(): Int = tracks.size
    fun updateTracks(newTracks: List<TrackUiModel>) {
        tracks = newTracks
        notifyDataSetChanged()
    }
}