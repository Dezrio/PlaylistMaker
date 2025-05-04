package com.example.playlistmaker.domain.search.api.repository

import com.example.playlistmaker.domain.search.models.Track

interface TracksHistoryRepository {
    fun getHistory() : List<Track>

    fun updateHistory(tracks: List<Track>)
}