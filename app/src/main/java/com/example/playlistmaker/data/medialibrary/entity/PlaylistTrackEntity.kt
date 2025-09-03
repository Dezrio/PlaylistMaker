package com.example.playlistmaker.data.medialibrary.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_track_table")
data class PlaylistTrackEntity(
    @PrimaryKey
    val trackId: Int,
    val artistName: String,
    val collectionName: String?,
    val trackName: String,
    val artworkUrl100: String,
    val trackTime: String,
    val country: String?,
    val primaryGenreName: String?,
    val releaseDate: String?,
    val previewUrl: String?,
    val createDate: Long
)