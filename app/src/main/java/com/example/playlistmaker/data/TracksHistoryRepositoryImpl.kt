package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.repository.TracksHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import java.lang.reflect.Type
import com.google.gson.reflect.TypeToken

class TracksHistoryRepositoryImpl(private val sharedPrefs: SharedPreferences) : TracksHistoryRepository {

    override fun getHistory(): List<Track> {
        val json = sharedPrefs.getString(HISTORY_TRACK_LIST_KEY, null)
        return if (json != null) {
            val type: Type = object : TypeToken<List<Track>>() {}.type
            Gson().fromJson(json, type) ?: listOf()
        } else {
            listOf()
        }
    }

    override fun updateHistory(tracks: List<Track>) {
        val json: String = Gson().toJson(tracks)
        sharedPrefs.edit()
            .putString(HISTORY_TRACK_LIST_KEY, json)
            .apply()
    }

    private companion object {
        const val HISTORY_TRACK_LIST_KEY = "HISTORY_TRACK_LIST_KEY"
    }
}