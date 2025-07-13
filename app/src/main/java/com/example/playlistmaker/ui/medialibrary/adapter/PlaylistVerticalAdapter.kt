package com.example.playlistmaker.ui.medialibrary.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.domain.medialibrary.models.Playlist

class PlaylistVerticalAdapter (private val onClickListener: OnClickListener) :
    RecyclerView.Adapter<PlaylistVerticalViewHolder>() {
    private val playlists: MutableList<Playlist> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistVerticalViewHolder {
        return PlaylistVerticalViewHolder(parent)
    }

    override fun onBindViewHolder(holder: PlaylistVerticalViewHolder, position: Int) {
        holder.bind(playlists[position])

        holder.itemView.setOnClickListener {
            onClickListener.onClick(playlists[position])
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    fun updatePlaylists(newPlaylists: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newPlaylists)
        this.notifyDataSetChanged()
    }

    fun interface OnClickListener {
        fun onClick(playlist: Playlist)
    }
}