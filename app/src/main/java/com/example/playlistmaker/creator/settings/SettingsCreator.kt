package com.example.playlistmaker.creator.settings

import android.app.Application
import com.example.playlistmaker.data.settings.SettingsRepositoryImpl
import com.example.playlistmaker.domain.settings.api.interactor.SettingsInteractor
import com.example.playlistmaker.domain.settings.api.repository.SettingsRepository
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl

object SettingsCreator {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        SettingsCreator.application = application
    }

    private fun getSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(application)
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }
}