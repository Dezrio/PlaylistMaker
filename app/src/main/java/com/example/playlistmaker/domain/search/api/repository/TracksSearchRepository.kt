package com.example.playlistmaker.domain.search.api.repository

import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksSearchRepository {
    fun searchTracks(trackName: String): Flow<List<Track>?>
}