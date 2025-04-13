package com.example.playlistmaker.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class TrackViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)) {
    private val trackName: TextView = itemView.findViewById(R.id.vhTrackName)
    private val artistName: TextView = itemView.findViewById(R.id.vhArtistName)
    private val trackTime: TextView = itemView.findViewById(R.id.vhTrackTime)
    private val artworkUrl100: ImageView = itemView.findViewById(R.id.ivArtworkUrl100)

    fun bind(item: Track){
        trackName.text = item.trackName
        artistName.text = item.artistName
        trackTime.text = item.trackTime

        Glide.with(itemView)
            .load(item.artworkUrl100)
            .fitCenter()
            .transform(RoundedCorners(2))
            .placeholder(R.drawable.track_placeholder)
            .into(artworkUrl100)
    }
}