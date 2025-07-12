package com.example.playlistmaker.data.favorites.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_track_table")
data class TrackEntity (
    @PrimaryKey
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val collectionName: String?,
    val country: String?,
    val primaryGenreName: String?,
    val releaseDate: String?,
    val previewUrl: String?,
    val createDate: Long
)