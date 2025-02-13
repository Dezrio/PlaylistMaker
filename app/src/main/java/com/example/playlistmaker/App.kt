package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    private var isDarkTheme = false
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        sharedPrefs = getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE)

        val isDarkModeOn = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
                Configuration.UI_MODE_NIGHT_YES

        isDarkTheme = sharedPrefs.getBoolean(DARK_THEME_KEY, isDarkModeOn) ?: false;
        switchTheme(isDarkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        isDarkTheme = darkThemeEnabled

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        sharedPrefs.edit()
            .putBoolean(DARK_THEME_KEY, darkThemeEnabled)
            .apply()
    }

    fun getSharedPreferences(): SharedPreferences {
        return  sharedPrefs;
    }

    fun isDarkTheme(): Boolean{
        return isDarkTheme
    }

    companion object {
        const val SHARED_PREFERENCES_FILE = "SHARED_PREFERENCES_FILE"
        const val DARK_THEME_KEY = "DARK_THEME_KEY"
        const val TRACK_KEY:String = "TRACK_KEY"
    }
}