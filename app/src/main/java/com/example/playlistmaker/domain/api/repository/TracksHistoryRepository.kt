package com.example.playlistmaker.domain.api.repository

import com.example.playlistmaker.domain.models.Track

interface TracksHistoryRepository {
    fun getHistory() : List<Track>

    fun updateHistory(tracks: List<Track>)
}