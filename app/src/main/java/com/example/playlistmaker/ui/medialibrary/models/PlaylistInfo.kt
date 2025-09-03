package com.example.playlistmaker.ui.medialibrary.models

import com.example.playlistmaker.domain.search.models.Track

data class PlaylistInfo(
    val title: String,
    val description: String,
    val coverPath: String,
    val tracks: List<Track>,
    val tracksCountString: String,
    val tracksDuration: String
)