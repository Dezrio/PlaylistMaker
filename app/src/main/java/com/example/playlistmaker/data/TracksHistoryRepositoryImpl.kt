package com.example.playlistmaker.data

import android.app.Application
import android.app.Application.MODE_PRIVATE
import com.example.playlistmaker.domain.api.repository.TracksHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class TracksHistoryRepositoryImpl(private val application: Application) : TracksHistoryRepository {

    override fun getHistory(): List<Track> {
        val json = application.getSharedPreferences(HISTORY_SHARED_PREFERENCES_FILE, MODE_PRIVATE).getString(HISTORY_TRACK_LIST_KEY, null)
        return if (json != null) {
            val type: Type = object : TypeToken<List<Track>>() {}.type
            Gson().fromJson(json, type) ?: listOf()
        } else {
            listOf()
        }
    }

    override fun updateHistory(tracks: List<Track>) {
        val json: String = Gson().toJson(tracks)
        application.getSharedPreferences(HISTORY_SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit()
            .putString(HISTORY_TRACK_LIST_KEY, json)
            .apply()
    }

    private companion object {
        const val HISTORY_TRACK_LIST_KEY = "HISTORY_TRACK_LIST_KEY"
        const val HISTORY_SHARED_PREFERENCES_FILE = "HISTORY_SHARED_PREFERENCES_FILE"
    }
}