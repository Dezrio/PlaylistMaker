package com.example.playlistmaker.ui.medialibrary.view_model

sealed class FavouriteTracksScreenState {
    data object Default: FavouriteTracksScreenState()
    data object NotFound: FavouriteTracksScreenState()
}