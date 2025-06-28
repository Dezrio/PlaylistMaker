package com.example.playlistmaker.domain.search.api.interactor

import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksSearchInteractor {
    fun searchTracks(trackName: String) : Flow<List<Track>?>
}