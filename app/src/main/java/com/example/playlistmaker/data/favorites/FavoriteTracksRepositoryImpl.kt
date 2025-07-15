package com.example.playlistmaker.data.favorites

import com.example.playlistmaker.data.favorites.converters.TrackDbConverter
import com.example.playlistmaker.data.favorites.dao.TrackDao
import com.example.playlistmaker.data.favorites.entity.TrackEntity
import com.example.playlistmaker.domain.favorites.api.repository.FavoriteTracksRepository
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val dao: TrackDao,
    private val trackConvertor: TrackDbConverter
) : FavoriteTracksRepository {
    override suspend fun saveTrack(track: Track) {
        dao.insert(trackConvertor.map(track))
    }

    override suspend fun deleteTrack(track: Track) {
        dao.delete(trackConvertor.map(track))
    }

    override fun getAllTracks(): Flow<List<Track>> {
        return dao.getAll().map { convertFromTrackEntity(it) }
    }

    override fun getTrackById(trackId: Int): Flow<Track> {
        return dao.getById(trackId).map { track ->
            if (track != null)
                trackConvertor.map(track)
            else
                trackConvertor.getEmpty()
        }
    }

    override fun isFavorite(trackId: Int): Flow<Boolean> {
        return dao.isFavorite(trackId)
    }

    private fun convertFromTrackEntity(entities: List<TrackEntity>): List<Track> {
        val tracks = entities.sortedByDescending { it.createDate }
            .map { entity -> trackConvertor.map(entity) }

        val markedTracks: MutableList<Track> = mutableListOf()

        for (i in tracks.indices)
            markedTracks.add(tracks[i].copy(isFavorite = true))

        return markedTracks
    }
}