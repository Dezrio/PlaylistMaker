package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.search.api.interactor.TracksSearchInteractor
import com.example.playlistmaker.domain.search.api.repository.TracksSearchRepository
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

class TracksSearchInteractorImpl(private val repository: TracksSearchRepository) :
    TracksSearchInteractor {
    override fun searchTracks(trackName: String) : Flow<List<Track>?> {
            return repository.searchTracks(trackName)
    }
}