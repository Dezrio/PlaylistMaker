package com.example.playlistmaker.domain.medialibrary.models

data class Playlist(
    val id: Int = 0,
    val title: String,
    val description: String,
    val coverPath: String,
    val tracksIds: List<Int>,
    val tracksCount: Int
)