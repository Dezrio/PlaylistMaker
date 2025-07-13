package com.example.playlistmaker.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.favorites.dao.TrackDao
import com.example.playlistmaker.data.favorites.entity.TrackEntity
import com.example.playlistmaker.data.medialibrary.dao.PlaylistDao
import com.example.playlistmaker.data.medialibrary.dao.PlaylistTrackDao
import com.example.playlistmaker.data.medialibrary.entity.PlaylistEntity
import com.example.playlistmaker.data.medialibrary.entity.PlaylistTrackEntity

@Database(version = 2,
    entities = [TrackEntity::class, PlaylistEntity::class, PlaylistTrackEntity::class],
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ],
    exportSchema = true)
abstract class AppDatabase : RoomDatabase(){
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackDao(): PlaylistTrackDao
}