package com.example.playlistmaker.data.settings

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.settings.api.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val isDefaultThemeDark: Boolean,
    private val sharedPrefs: SharedPreferences
    ) : SettingsRepository {

    override fun isDarkTheme(): Boolean {
        return sharedPrefs.getBoolean(DARK_THEME_KEY, isDefaultThemeDark)
    }

    override fun saveTheme(isDarkTheme: Boolean) {
        sharedPrefs.edit()
            .putBoolean(DARK_THEME_KEY, isDarkTheme)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    companion object {
        const val DARK_THEME_KEY = "DARK_THEME_KEY"
    }
}