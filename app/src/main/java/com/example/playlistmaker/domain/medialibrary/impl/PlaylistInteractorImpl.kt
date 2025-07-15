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

    override suspend fun deleteTrack(playlist: Playlist, track: Track) {
        playlistRepository.deleteTrack(playlist, track)
    }

    override fun checkPlaylistExistence(playlistTitle: String): Flow<Boolean> {
        return playlistRepository.checkPlaylistExistence(playlistTitle)
    }
}