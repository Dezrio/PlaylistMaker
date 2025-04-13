package com.example.playlistmaker.domain.api.repository

import com.example.playlistmaker.domain.models.Track

interface TracksSearchRepository {
    fun searchTracks(trackName: String): List<Track>?
}