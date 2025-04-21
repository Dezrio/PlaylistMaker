package com.example.playlistmaker.domain.api.repository

interface SettingsRepository {
    fun isDarkTheme(): Boolean

    fun saveTheme(isDarkTheme: Boolean)
}