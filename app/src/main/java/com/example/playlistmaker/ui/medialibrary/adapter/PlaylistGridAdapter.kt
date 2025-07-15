package com.example.playlistmaker.ui.medialibrary.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.domain.medialibrary.models.Playlist

class PlaylistGridAdapter(private val onLongClickListener: OnLongClickListener) :
    RecyclerView.Adapter<PlaylistGridViewHolder>() {
    private val playlists: MutableList<Playlist> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistGridViewHolder{
        return PlaylistGridViewHolder(parent)
    }

    override fun onBindViewHolder(holder: PlaylistGridViewHolder, position: Int) {
        holder.bind(playlists[position])

        holder.itemView.setOnLongClickListener {
            onLongClickListener.onLongClick(holder.itemView, playlists[position])
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

    fun interface OnLongClickListener {
        fun onLongClick(view: View, playlist: Playlist): Boolean
    }
}