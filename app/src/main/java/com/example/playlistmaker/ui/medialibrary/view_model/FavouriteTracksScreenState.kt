package com.example.playlistmaker.ui.medialibrary.view_model

sealed interface FavouriteTracksScreenState {
    data object Default: FavouriteTracksScreenState
    data object NotFound: FavouriteTracksScreenState
}