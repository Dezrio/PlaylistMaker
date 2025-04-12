package com.example.playlistmaker.creator

import android.content.SharedPreferences
import com.example.playlistmaker.data.TracksHistoryRepositoryImpl
import com.example.playlistmaker.domain.api.interactor.TracksHistoryInteractor
import com.example.playlistmaker.domain.api.repository.TracksHistoryRepository
import com.example.playlistmaker.domain.impl.TracksHistoryInteractorImpl

object HistoryCreator {
    private fun getTrackRepositoryHistory(sharedPrefs: SharedPreferences): TracksHistoryRepository {
        return TracksHistoryRepositoryImpl(sharedPrefs)
    }

    fun provideTracksHistoryInteractor(sharedPrefs: SharedPreferences): TracksHistoryInteractor {
        return TracksHistoryInteractorImpl(getTrackRepositoryHistory(sharedPrefs))
    }
}