package com.example.playlistmaker.bind.search

import dataclasses.Track

class TrackAdapter(private val data: MutableList<Track>, private val onClick: (Track) -> Unit) : BaseTrackAdapter(data) {
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(data[position])
        holder.itemView.setOnClickListener { onClick(data[position]) }
    }
}