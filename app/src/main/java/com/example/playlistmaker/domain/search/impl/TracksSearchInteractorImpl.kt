package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.search.api.interactor.TracksSearchInteractor
import com.example.playlistmaker.domain.search.api.repository.TracksSearchRepository
import java.util.concurrent.Executors

class TracksSearchInteractorImpl(private val repository: TracksSearchRepository) :
    TracksSearchInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(trackName: String, consumer: TracksSearchInteractor.TrackConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(trackName))
        }
    }
}