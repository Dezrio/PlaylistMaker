package com.example.playlistmaker.domain.search.api.interactor

import com.example.playlistmaker.domain.search.models.Track

interface TracksHistoryInteractor {
    fun getHistory(): List<Track>

    fun updateHistory(newTrack: Track)

    fun clearHistory()
}