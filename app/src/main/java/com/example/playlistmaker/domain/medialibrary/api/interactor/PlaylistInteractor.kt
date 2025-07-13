package com.example.playlistmaker.domain.medialibrary.api.interactor

import com.example.playlistmaker.domain.medialibrary.models.Playlist
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun create(playlist: Playlist)

    fun getAll(): Flow<List<Playlist>>

    suspend fun delete(playlist: Playlist)

    suspend fun addNewTrack(playlist: Playlist, track: Track)

    suspend fun deleteTrack(playlist: Playlist, track: Track)

    fun checkPlaylistExistence(playlistTitle: String): Flow<Boolean>
}