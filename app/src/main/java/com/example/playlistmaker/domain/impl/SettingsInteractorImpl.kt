package com.example.playlistmaker.domain.impl

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.api.interactor.SettingsInteractor
import com.example.playlistmaker.domain.api.repository.SettingsRepository

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {
    override fun isDarkTheme(): Boolean {
        return repository.isDarkTheme()
    }

    override fun switchTheme(isDarkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        repository.saveTheme(isDarkTheme)
    }
}