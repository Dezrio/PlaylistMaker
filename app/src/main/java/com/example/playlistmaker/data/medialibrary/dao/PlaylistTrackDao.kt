package com.example.playlistmaker.data.medialibrary.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.medialibrary.entity.PlaylistTrackEntity

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(track: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_track_table")
    suspend fun getAll(): List<PlaylistTrackEntity>

    @Query("DELETE FROM playlist_track_table WHERE trackId = :trackId")
    suspend fun delete(trackId: Int)
}