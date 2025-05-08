package com.example.playlistmaker.creator.search

import android.app.Application
import com.example.playlistmaker.data.search.TracksHistoryRepositoryImpl
import com.example.playlistmaker.domain.search.api.interactor.TracksHistoryInteractor
import com.example.playlistmaker.domain.search.api.repository.TracksHistoryRepository
import com.example.playlistmaker.domain.search.impl.TracksHistoryInteractorImpl

object HistoryCreator {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        HistoryCreator.application = application
    }

    private fun getTrackRepositoryHistory(): TracksHistoryRepository {
        return TracksHistoryRepositoryImpl(application)
    }

    fun provideTracksHistoryInteractor(): TracksHistoryInteractor {
        return TracksHistoryInteractorImpl(getTrackRepositoryHistory())
    }
}