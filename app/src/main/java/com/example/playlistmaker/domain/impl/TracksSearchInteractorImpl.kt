package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.interactor.TracksSearchInteractor
import com.example.playlistmaker.domain.api.repository.TracksSearchRepository
import java.util.concurrent.Executors

class TracksSearchInteractorImpl(private val repository: TracksSearchRepository) : TracksSearchInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(text: String, consumer: TracksSearchInteractor.TrackConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(text))
        }
    }
}