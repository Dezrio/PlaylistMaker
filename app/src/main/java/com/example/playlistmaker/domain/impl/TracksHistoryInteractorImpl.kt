package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.interactor.TracksHistoryInteractor
import com.example.playlistmaker.domain.api.repository.TracksHistoryRepository
import com.example.playlistmaker.domain.models.Track

class TracksHistoryInteractorImpl(private val repository: TracksHistoryRepository) : TracksHistoryInteractor {
    override fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun updateHistory(newTrack: Track) {
        var tracks: MutableList<Track> = getHistory().toMutableList()

        if (indexOfTheSame(tracks, newTrack) != -1)
            tracks.remove(newTrack)

        tracks.add(0, newTrack)

        if (tracks.size > MAX_TRACKS_COUNT)
            tracks = tracks.take(MAX_TRACKS_COUNT).toMutableList()

        repository.updateHistory(tracks)
    }

    override fun clearHistory() {
        repository.updateHistory(listOf())
    }

    private fun indexOfTheSame(tracks: List<Track>, track: Track): Int {
        tracks.forEachIndexed { index, element ->
            if (element.trackId == track.trackId)
                return index
        }

        return -1
    }

    private companion object {
        const val MAX_TRACKS_COUNT = 10
    }
}