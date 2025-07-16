package com.example.playlistmaker.ui.medialibrary.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistVerticalItemBinding
import com.example.playlistmaker.domain.medialibrary.models.Playlist

class PlaylistVerticalViewHolder(
    parent: ViewGroup, private val binding: PlaylistVerticalItemBinding =
        PlaylistVerticalItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        Glide.with(itemView)
            .load(playlist.coverPath)
            .placeholder(R.drawable.player_placeholder)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(2)))
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(binding.ivPlaylistCover)

        binding.tvPlaylistTitle.text = playlist.title
        binding.tvPlaylistTracksCount.text = itemView.resources.getQuantityString(R.plurals.track_plurals, playlist.tracksCount, playlist.tracksCount)
    }
}