package com.example.playlistmaker.data.favorites

import com.example.playlistmaker.data.favorites.converters.TrackDbConvertor
import com.example.playlistmaker.data.favorites.entity.TrackEntity
import com.example.playlistmaker.domain.favorites.api.repository.FavoriteTracksRepository
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackConvertor: TrackDbConvertor
) : FavoriteTracksRepository {
    override suspend fun saveTrack(track: Track) {
        appDatabase.trackDao().insert(trackConvertor.map(track))
    }

    override suspend fun deleteTrack(track: Track) {
        appDatabase.trackDao().delete(trackConvertor.map(track))
    }

    override fun getAllTracks(): Flow<List<Track>> {
        return appDatabase.trackDao().getAll().map { convertFromTrackEntity(it) }
    }

    override fun getTrackById(trackId: Int): Flow<Track> {
        return appDatabase.trackDao().getById(trackId).map { track ->
            if (track != null)
                trackConvertor.map(track)
            else
                trackConvertor.getEmpty()
        }
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