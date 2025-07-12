package com.example.playlistmaker.domain.favorites.impl

import com.example.playlistmaker.domain.favorites.api.interactor.FavoriteTracksInteractor
import com.example.playlistmaker.domain.favorites.api.repository.FavoriteTracksRepository
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    val tracksRepository: FavoriteTracksRepository) : FavoriteTracksInteractor
{
    override suspend fun saveTrack(track: Track) {
        tracksRepository.saveTrack(track)
    }

    override suspend fun deleteTrack(track: Track) {
        tracksRepository.deleteTrack(track)
    }

    override fun getAllTracks(): Flow<List<Track>> {
        return tracksRepository.getAllTracks()
    }

    override fun getTrackById(trackId: Int): Flow<Track> {
        return tracksRepository.getTrackById(trackId)
    }

    override fun isFavorite(trackId: Int): Flow<Boolean> {
        return tracksRepository.isFavorite(trackId)
    }
}