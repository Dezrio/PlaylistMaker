package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.data.network.consts.SearchResponseStates.Companion.SUCCESS
import com.example.playlistmaker.domain.api.repository.TracksSearchRepository
import com.example.playlistmaker.domain.models.Track

class TracksSearchRepositoryImpl(private val networkClient: NetworkClient) : TracksSearchRepository {
    override fun searchTracks(text: String): List<Track>? {
        val response = networkClient.doRequest(TracksSearchRequest(text))
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