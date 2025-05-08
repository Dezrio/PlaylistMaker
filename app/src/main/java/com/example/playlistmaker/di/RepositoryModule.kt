package com.example.playlistmaker.di

import android.content.res.Configuration
import com.example.playlistmaker.data.search.TracksHistoryRepositoryImpl
import com.example.playlistmaker.data.search.TracksSearchRepositoryImpl
import com.example.playlistmaker.data.settings.SettingsRepositoryImpl
import com.example.playlistmaker.data.sharing.LinkManagerInteractorImpl
import com.example.playlistmaker.domain.search.api.repository.TracksHistoryRepository
import com.example.playlistmaker.domain.search.api.repository.TracksSearchRepository
import com.example.playlistmaker.domain.settings.api.repository.SettingsRepository
import com.example.playlistmaker.ui.App.Companion.DI_SHARED_PREFS_HISTORY
import com.example.playlistmaker.ui.App.Companion.DI_SHARED_PREFS_SETTINGS
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module

val RepositoryModule = module {
    single<TracksHistoryRepository> {
        TracksHistoryRepositoryImpl(get(), get(named(DI_SHARED_PREFS_HISTORY)))
    }

    single<TracksSearchRepository> {
        TracksSearchRepositoryImpl(get())
    }

    single<SettingsRepository> {
        val isDefaultThemeDark = androidApplication().resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        SettingsRepositoryImpl(isDefaultThemeDark, get(named(DI_SHARED_PREFS_SETTINGS)))
    }

    single<LinkManagerInteractorImpl> {
        LinkManagerInteractorImpl(androidApplication())
    }
}