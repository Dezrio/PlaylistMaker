package com.example.playlistmaker.data.medialibrary.converters

import com.example.playlistmaker.data.medialibrary.entity.PlaylistEntity
import com.example.playlistmaker.domain.medialibrary.models.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class PlaylistDbConverter(private val gson: Gson) {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            title = playlist.title,
            description = playlist.description,
            coverPath = playlist.coverPath,
            tracksIds = convertIntsToString(playlist.tracksIds),
            tracksCount = playlist.tracksCount
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            id = playlist.id,
            title = playlist.title,
            description = playlist.description,
            coverPath = playlist.coverPath,
            tracksIds = convertStringToInts(playlist.tracksIds),
            tracksCount = playlist.tracksCount
        )
    }

    fun mapList(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { this.map(it) }
    }

    fun convertIntsToString(list: List<Int>): String {
        return gson.toJson(list)
    }

    fun convertStringToInts(str: String): List<Int> {
        val type: Type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(str, type)
    }

    companion object {
        fun empty(): Playlist {
            return Playlist(
                title = "",
                description = "",
                coverPath = "",
                tracksIds = listOf(),
                tracksCount = 0
            )
        }
    }
}