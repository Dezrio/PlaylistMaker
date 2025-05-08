package com.example.playlistmaker.domain.search.api.repository

import com.example.playlistmaker.domain.search.models.Track

interface TracksSearchRepository {
    fun searchTracks(trackName: String): List<Track>?
}