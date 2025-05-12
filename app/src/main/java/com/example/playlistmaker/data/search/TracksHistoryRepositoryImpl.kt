package com.example.playlistmaker.data.search

import android.app.Application
import android.app.Application.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.playlistmaker.domain.search.api.repository.TracksHistoryRepository
import com.example.playlistmaker.domain.search.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class TracksHistoryRepositoryImpl(
    private val gson: Gson,
    private val sharedPrefsFile: SharedPreferences
) : TracksHistoryRepository {

    override fun getHistory(): List<Track> {
        val json = sharedPrefsFile.getString(
            HISTORY_TRACK_LIST_KEY, null)
        return json?.let {
            val type: Type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } ?: emptyList()
    }

    override fun updateHistory(tracks: List<Track>) {
        val json: String = gson.toJson(tracks)
        sharedPrefsFile.edit()
            .putString(HISTORY_TRACK_LIST_KEY, json)
            .apply()
    }

    private companion object {
        const val HISTORY_TRACK_LIST_KEY = "HISTORY_TRACK_LIST_KEY"
    }
}