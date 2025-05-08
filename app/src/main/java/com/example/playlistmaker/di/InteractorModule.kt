package com.example.playlistmaker.di

import com.example.playlistmaker.data.sharing.LinkManagerInteractorImpl
import com.example.playlistmaker.domain.player.api.interactor.AudioPlayerInteractor
import com.example.playlistmaker.domain.player.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.domain.search.api.interactor.TracksHistoryInteractor
import com.example.playlistmaker.domain.search.api.interactor.TracksSearchInteractor
import com.example.playlistmaker.domain.search.impl.TracksHistoryInteractorImpl
import com.example.playlistmaker.domain.search.impl.TracksSearchInteractorImpl
import com.example.playlistmaker.domain.settings.api.interactor.SettingsInteractor
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.sharing.api.interactor.LinkManagerInteractor
import org.koin.dsl.module

val InteractorModule = module {
    factory<AudioPlayerInteractor> {
        AudioPlayerInteractorImpl(get())
    }

    single<TracksHistoryInteractor> {
        TracksHistoryInteractorImpl(get())
    }

    single<TracksSearchInteractor> {
        TracksSearchInteractorImpl(get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single<LinkManagerInteractor> {
        LinkManagerInteractorImpl(get())
    }
}