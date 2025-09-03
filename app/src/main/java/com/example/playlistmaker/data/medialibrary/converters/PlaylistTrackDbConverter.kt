package com.example.playlistmaker.data.medialibrary.converters

import com.example.playlistmaker.data.medialibrary.entity.PlaylistTrackEntity
import com.example.playlistmaker.domain.search.models.Track

class PlaylistTrackDbConverter {
    fun map(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            trackId = track.trackId,
            artistName = track.trackName,
            collectionName = track.collectionName,
            trackName = track.trackName,
            artworkUrl100 = track.artworkUrl100,
            trackTime = track.trackTime,
            country = track.country,
            primaryGenreName = track.primaryGenreName,
            releaseDate = track.releaseDate,
            previewUrl = track.previewUrl,
            createDate = System.currentTimeMillis()
        )
    }

    fun map(track: PlaylistTrackEntity): Track {
        return Track(
            trackId = track.trackId,
            artistName = track.trackName,
            collectionName = track.collectionName,
            trackName = track.trackName,
            artworkUrl100 = track.artworkUrl100,
            artworkUrl512 = getCoverArtwork(track.artworkUrl100),
            trackTime = track.trackTime,
            country = track.country,
            primaryGenreName = track.primaryGenreName,
            releaseDate = track.releaseDate,
            previewUrl = track.previewUrl
        )
    }

    fun getEmpty(): Track{
        return Track(
            trackId = 0,
            artistName = "",
            collectionName = "",
            trackName = "",
            artworkUrl100 = "",
            artworkUrl512 = "",
            trackTime = "",
            country = "",
            primaryGenreName = "",
            releaseDate = "",
            previewUrl = "")
    }

    private fun getCoverArtwork(artworkUrl100: String) = artworkUrl100.replaceAfterLast(COVER_DELIMITER, COVER_REPLACEMENT)

    private companion object {
        const val COVER_DELIMITER = '/'
        const val COVER_REPLACEMENT = "512x512bb.jpg"
    }
}