package com.example.playlistmaker.data.search

import com.example.playlistmaker.data.search.dto.TracksSearchRequest
import com.example.playlistmaker.data.search.dto.TracksSearchResponse
import com.example.playlistmaker.data.search.network.consts.SearchResponseStates.SUCCESS
import com.example.playlistmaker.domain.search.api.repository.TracksSearchRepository
import com.example.playlistmaker.domain.search.models.Track

class TracksSearchRepositoryImpl(private val networkClient: NetworkClient) :
    TracksSearchRepository {
    override fun searchTracks(trackName: String): List<Track>? {
        val response = networkClient.doRequest(TracksSearchRequest(trackName))
        return if (response.resultCode == SUCCESS) {
            (response as TracksSearchResponse).results.map {
                Track(
                    it.trackId,
                    it.artistName,
                    it.collectionName,
                    it.trackName,
                    it.artworkUrl100,
                    it.getCoverArtwork(),
                    it.getTrackTime(),
                    it.country,
                    it.primaryGenreName,
                    it.getTrackYear(),
                    it.previewUrl
                )
            }
        } else {
            null
        }
    }
}