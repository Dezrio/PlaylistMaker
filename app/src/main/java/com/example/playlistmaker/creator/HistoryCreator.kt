package com.example.playlistmaker.creator

import android.app.Application
import com.example.playlistmaker.data.TracksHistoryRepositoryImpl
import com.example.playlistmaker.domain.api.interactor.TracksHistoryInteractor
import com.example.playlistmaker.domain.api.repository.TracksHistoryRepository
import com.example.playlistmaker.domain.impl.TracksHistoryInteractorImpl

object HistoryCreator {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    private fun getTrackRepositoryHistory(): TracksHistoryRepository {
        return TracksHistoryRepositoryImpl(application)
    }

    fun provideTracksHistoryInteractor(): TracksHistoryInteractor {
        return TracksHistoryInteractorImpl(getTrackRepositoryHistory())
    }
}