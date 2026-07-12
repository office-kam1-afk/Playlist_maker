package com.example.playlistmaker


import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(private var tracks: List<Track>) : RecyclerView.Adapter<TrackViewHolder>() {


    var onTrackClick: ((Track) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)

        holder.itemView.setOnClickListener {

            val intent = Intent(holder.itemView.context, PlayerActivity::class.java)
            intent.putExtra("track", track)
           holder.itemView.context.startActivity(intent)
            onTrackClick?.invoke(track)
        }
    }

    override fun getItemCount(): Int = tracks.size


    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
      }
}