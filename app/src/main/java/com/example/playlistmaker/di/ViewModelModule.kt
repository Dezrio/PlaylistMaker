package com.example.playlistmaker.di

import com.example.playlistmaker.ui.medialibrary.view_model.FavouriteTracksFragmentViewModel
import com.example.playlistmaker.ui.medialibrary.view_model.PlaylistsFragmentViewModel
import com.example.playlistmaker.ui.player.view_model.AudioPlayerViewModel
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val ViewModelModule = module {
    viewModel { (trackId: Int) ->
        AudioPlayerViewModel(trackId, get(), get())
    }

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel{
        FavouriteTracksFragmentViewModel()
    }

    viewModel {
        PlaylistsFragmentViewModel()
    }
}