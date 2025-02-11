package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dataclasses.Track
import java.lang.reflect.Type


class SearchHistoryHandler(private val sharedPrefs: SharedPreferences) {
    private var tracks: MutableList<Track> = getSharedPreferencesTracks().toMutableList()

    fun saveTrack(track: Track) {
        tracks.clear()
        tracks.addAll(getSharedPreferencesTracks().toMutableList())

        if (indexOfTheSame(tracks, track) != -1)
            tracks.remove(track)

        tracks.add(0, track)

        if (tracks.size > MAX_TRACKS_COUNT)
            tracks = tracks.take(MAX_TRACKS_COUNT).toMutableList()

        writeSharedPreferencesTracks(tracks)
    }

    fun getSearchHistoryTracks(): MutableList<Track> {
        return tracks
    }

    fun clearSearchHistory() {
        writeSharedPreferencesTracks(emptyList())
        tracks.clear()
    }

    private fun writeSharedPreferencesTracks(tracks: List<Track>) {
        sharedPrefs.edit()
            .putString(HISTORY_TRACK_LIST_KEY, Gson().toJson(tracks))
            .apply()
    }

    private fun getSharedPreferencesTracks(): List<Track> {
        val json = sharedPrefs.getString(HISTORY_TRACK_LIST_KEY, null)
        return if (json != null) {
            val type: Type = object : TypeToken<List<Track>>() {}.type
            Gson().fromJson(json, type) ?: listOf()
        } else {
            listOf()
        }
    }

    private fun indexOfTheSame(tracks: List<Track>, track: Track): Int {
        tracks.forEachIndexed { index, element ->
            if (element.trackId == track.trackId)
                return index
        }

        return -1
    }

    private companion object {
        const val MAX_TRACKS_COUNT = 10
        const val HISTORY_TRACK_LIST_KEY = "HISTORY_TRACK_LIST_KEY"
    }
}