package com.example.playlistmaker.data.settings

import android.app.Application
import android.app.Application.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.res.Configuration
import com.example.playlistmaker.domain.settings.api.repository.SettingsRepository

class SettingsRepositoryImpl : SettingsRepository {
    private val application: Application;
    private val sharedPrefs: SharedPreferences;

    constructor(application: Application) {
        this.application = application
        this.sharedPrefs = application.getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE)
    }

    override fun isDarkTheme(): Boolean {
        val isDefaultThemeDark = application.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        return sharedPrefs.getBoolean(DARK_THEME_KEY, isDefaultThemeDark)
    }

    override fun saveTheme(isDarkTheme: Boolean) {
        sharedPrefs.edit()
            .putBoolean(DARK_THEME_KEY, isDarkTheme)
            .apply()
    }

    companion object {
        const val SHARED_PREFERENCES_FILE = "SHARED_PREFERENCES_FILE"
        const val DARK_THEME_KEY = "DARK_THEME_KEY"
    }
}