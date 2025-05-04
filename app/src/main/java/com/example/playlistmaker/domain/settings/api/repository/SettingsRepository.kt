package com.example.playlistmaker.domain.settings.api.repository

interface SettingsRepository {
    fun isDarkTheme(): Boolean

    fun saveTheme(isDarkTheme: Boolean)
}