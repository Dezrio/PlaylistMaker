package com.example.playlistmaker.domain.settings.api.interactor

interface SettingsInteractor {
    fun isDarkTheme(): Boolean

    fun switchTheme(isDarkTheme: Boolean)
}