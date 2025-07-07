package com.example.playlistmaker.data.favorites.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.favorites.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun delete(track: TrackEntity)

    @Query("SELECT * FROM favorite_track_table")
    fun getAll(): Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM favorite_track_table")
    suspend fun getAllIds() : List<Int>

    @Query("SELECT * FROM favorite_track_table WHERE trackId = :id")
    fun getById(id: Int) : Flow<TrackEntity?>
}