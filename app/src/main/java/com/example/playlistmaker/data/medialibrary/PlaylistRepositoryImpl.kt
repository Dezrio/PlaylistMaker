package com.example.playlistmaker.data.medialibrary

import com.example.playlistmaker.data.medialibrary.converters.PlaylistDbConverter
import com.example.playlistmaker.data.medialibrary.converters.PlaylistTrackDbConverter
import com.example.playlistmaker.data.medialibrary.dao.PlaylistDao
import com.example.playlistmaker.data.medialibrary.dao.PlaylistTrackDao
import com.example.playlistmaker.domain.medialibrary.api.repository.PlaylistRepository
import com.example.playlistmaker.domain.medialibrary.models.Playlist
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val playlistConverter: PlaylistDbConverter,
    private val playlistTrackConverter: PlaylistTrackDbConverter
) : PlaylistRepository {
    override suspend fun create(playlist: Playlist) {
        playlistDao.insertPlaylist(playlistConverter.map(playlist))
    }

    override suspend fun delete(playlist: Playlist) {
        for (trackId in playlist.tracksIds) {
            if (checkOnlyOnePlaylistHasTrack(playlist.id, trackId))
                playlistTrackDao.delete(trackId)
        }

        playlistDao.deletePlaylist(playlistConverter.map(playlist))
    }

    override fun getAll(): Flow<List<Playlist>> = flow {
        val playlists = playlistDao.getAllPlaylists()
        emit(playlistConverter.mapList(playlists))
    }

    override suspend fun addNewTrack(playlist: Playlist, track: Track) {
        val newTrackList: MutableList<Int> = mutableListOf()
        newTrackList.addAll(playlist.tracksIds)
        newTrackList.add(track.trackId)

        val updatedPlaylist =
            playlist.copy(tracksIds = newTrackList, tracksCount = newTrackList.size)

        playlistDao.updatePlaylist(playlistConverter.map(updatedPlaylist))

        playlistTrackDao.insert(playlistTrackConverter.map(track))
    }

    override suspend fun deleteTrack(playlist: Playlist, track: Track) {
        val newTrackList: MutableList<Int> = mutableListOf()
        newTrackList.addAll(playlist.tracksIds)
        newTrackList.remove(track.trackId)

        val updatedPlaylist =
            playlist.copy(tracksIds = newTrackList, tracksCount = newTrackList.size)

        playlistDao.updatePlaylist(playlistConverter.map(updatedPlaylist))

        if (checkOnlyOnePlaylistHasTrack(playlist.id, track.trackId))
            playlistTrackDao.delete(track.trackId)
    }

    override fun checkPlaylistExistence(playlistTitle: String): Flow<Boolean> = flow {
        val playlistsTitles = playlistDao.getAllPlaylistsTitles()
        emit(playlistsTitles.contains(playlistTitle))
    }

    private suspend fun checkOnlyOnePlaylistHasTrack(playlistId: Int, trackId: Int): Boolean {
        val playlists = playlistDao.getAllPlaylists()
        var isOnlyOnePlaylistHasTrack = true
        for (playlist in playlists) {
            if (playlist.id != playlistId) {
                val tracksIds =
                    playlistConverter.convertStringToInts(playlist.tracksIds).toMutableList()
                isOnlyOnePlaylistHasTrack = !tracksIds.contains(trackId)
            }
            if (!isOnlyOnePlaylistHasTrack) break
        }
        return isOnlyOnePlaylistHasTrack
    }
}