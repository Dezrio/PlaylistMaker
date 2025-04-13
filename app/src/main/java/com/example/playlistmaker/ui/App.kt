package com.example.playlistmaker.ui

import android.app.Application
import com.example.playlistmaker.creator.HistoryCreator
import com.example.playlistmaker.creator.LinkManagerCreator
import com.example.playlistmaker.creator.SettingsCreator
import com.example.playlistmaker.domain.api.interactor.SettingsInteractor

class App : Application() {
    private lateinit var settingsInteractorImpl: SettingsInteractor

    override fun onCreate() {
        super.onCreate()

        HistoryCreator.initApplication(this)
        LinkManagerCreator.initApplication(this)
        SettingsCreator.initApplication(this)

        settingsInteractorImpl = SettingsCreator.provideSettingsInteractor()
        switchTheme(settingsInteractorImpl.isDarkTheme())
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        settingsInteractorImpl.switchTheme(darkThemeEnabled)
    }

    companion object {
        const val TRACK_KEY:String = "TRACK_KEY"
    }
}