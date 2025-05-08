package com.example.playlistmaker.domain.search.api.interactor

import com.example.playlistmaker.domain.search.models.Track

interface TracksSearchInteractor {
    fun searchTracks(trackName: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTracks: List<Track>?)
    }
}