package com.example.playlistmaker.bind.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import dataclasses.Track

class TrackAdapter(private val data: MutableList<Track>) : RecyclerView.Adapter<TrackViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(data[position])
    }
}