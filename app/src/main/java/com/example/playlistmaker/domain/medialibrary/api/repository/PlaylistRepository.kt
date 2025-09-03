package com.example.playlistmaker.domain.medialibrary.api.repository

import com.example.playlistmaker.domain.medialibrary.models.Playlist
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun create(playlist: Playlist)

    fun getAll(): Flow<List<Playlist>>

    suspend fun delete(playlist: Playlist)

    suspend fun addNewTrack(playlist: Playlist, track: Track)

    suspend fun deleteTrack(playlist: Playlist, trackId: Int)

    fun checkPlaylistExistence(playlistTitle: String): Flow<Boolean>

    fun getById(playlistId: Int): Flow<Playlist>

    fun getTracksByIds(tracksIds: List<Int>): Flow<List<Track>>

    suspend fun update(playlist: Playlist)
}