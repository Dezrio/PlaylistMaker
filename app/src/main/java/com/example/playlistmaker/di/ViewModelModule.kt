package com.example.playlistmaker.di

import AddPlaylistFragmentViewModel
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.ui.favorites.view_model.FavouriteTracksFragmentViewModel
import com.example.playlistmaker.ui.medialibrary.view_model.PlaylistViewModel
import com.example.playlistmaker.ui.medialibrary.view_model.PlaylistsFragmentViewModel
import com.example.playlistmaker.ui.player.view_model.AudioPlayerViewModel
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import com.example.playlistmaker.utils.ResourcesProvider
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val ViewModelModule = module {
    viewModel { (track: Track) ->
        AudioPlayerViewModel(track, get(), get())
    }

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel{
        FavouriteTracksFragmentViewModel(get())
    }

    viewModel {
        PlaylistsFragmentViewModel(get())
    }

    single {
        ResourcesProvider(androidContext())
    }

    viewModel { (playlistId: Int) ->
        PlaylistViewModel(playlistId, get(), get(), get())
    }

    viewModel { (playlistId: Int) ->
        AddPlaylistFragmentViewModel(get(), playlistId)
    }
}