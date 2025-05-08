package com.example.playlistmaker.ui.search.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.domain.search.models.Track

class TrackAdapter(private val onClick: (Track) -> Unit) : RecyclerView.Adapter<TrackViewHolder>() {
    private val data: MutableList<Track> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(data[position])
        holder.itemView.setOnClickListener { onClick(data[position]) }
    }

    fun updateTracks(tracks: List<Track>){
        data.clear()
        data.addAll(tracks)
        this.notifyDataSetChanged()
    }
}