package com.example.playlistmaker.domain.medialibrary.impl

import com.example.playlistmaker.domain.medialibrary.api.interactor.PlaylistInteractor
import com.example.playlistmaker.domain.medialibrary.api.repository.PlaylistRepository
import com.example.playlistmaker.domain.medialibrary.models.Playlist
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {

    override suspend fun create(playlist: Playlist) {
        playlistRepository.create(playlist)
    }

    override suspend fun delete(playlist: Playlist) {
        playlistRepository.delete(playlist)
    }

    override fun getAll(): Flow<List<Playlist>> {
        return playlistRepository.getAll()
    }

    override suspend fun addNewTrack(playlist: Playlist, track: Track) {
        playlistRepository.addNewTrack(playlist, track)
    }

    override suspend fun deleteTrack(playlist: Playlist, trackId: Int) {
        playlistRepository.deleteTrack(playlist, trackId)
    }

    override fun checkPlaylistExistence(playlistTitle: String): Flow<Boolean> {
        return playlistRepository.checkPlaylistExistence(playlistTitle)
    }

    override fun getById(playlistId: Int): Flow<Playlist> {
        return playlistRepository.getById(playlistId)
    }

    override fun getTracksByIds(tracksIds: List<Int>): Flow<List<Track>> {
        return playlistRepository.getTracksByIds(tracksIds)
    }

    override suspend fun update(playlist: Playlist) {
        return playlistRepository.update(playlist)
    }
}