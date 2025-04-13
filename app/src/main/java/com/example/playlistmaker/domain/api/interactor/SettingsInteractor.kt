package com.example.playlistmaker.domain.api.interactor

interface SettingsInteractor {
    fun isDarkTheme(): Boolean

    fun switchTheme(isDarkTheme: Boolean)
}