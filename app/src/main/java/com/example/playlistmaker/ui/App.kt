package com.example.playlistmaker.ui

import android.app.Application
import com.example.playlistmaker.di.DataModule
import com.example.playlistmaker.di.InteractorModule
import com.example.playlistmaker.di.RepositoryModule
import com.example.playlistmaker.di.ViewModelModule
import com.example.playlistmaker.domain.settings.api.interactor.SettingsInteractor
import com.markodevcic.peko.PermissionRequester
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin


class App : Application() {
     override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(DataModule, RepositoryModule, InteractorModule, ViewModelModule)
        }

        val settingsInteractorImpl: SettingsInteractor = getKoin().get()
        settingsInteractorImpl.switchTheme(settingsInteractorImpl.isDarkTheme())
        PermissionRequester.initialize(applicationContext)
    }

    companion object {
        const val EXTERNAL_STORAGE_NAME :String = "playlist_maker"
        const val DI_SHARED_PREFS_SETTINGS :String = "DI_SHARED_PREFS_SETTINGS "
        const val DI_SHARED_PREFS_HISTORY :String = "DI_SHARED_PREFS_HISTORY "
        const val AUDIO_PLAYER_INTENT_TRACK_URL :String = "AUDIO_PLAYER_INTENT_TRACK_URL"
        const val AUDIO_PLAYER_INTENT_TRACK_ARTIST_NAME :String = "AUDIO_PLAYER_INTENT_TRACK_ARTIST_NAME"
        const val AUDIO_PLAYER_INTENT_TRACK_TITLE :String = "AUDIO_PLAYER_INTENT_TRACK_TITLE"
    }
}