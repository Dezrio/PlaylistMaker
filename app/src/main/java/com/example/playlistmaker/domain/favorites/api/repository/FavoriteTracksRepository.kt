package com.example.playlistmaker.domain.favorites.api.repository

import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {
    suspend fun saveTrack(track: Track)

    suspend fun deleteTrack(track: Track)

    fun getAllTracks(): Flow<List<Track>>

    fun getTrackById(trackId: Int): Flow<Track>

    fun isFavorite(trackId: Int): Flow<Boolean>
}