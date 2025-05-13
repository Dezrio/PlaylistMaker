package com.example.playlistmaker.ui.medialibrary.view_model

sealed interface PlaylistsScreenState {
    data object Default: PlaylistsScreenState
    data object NotFound: PlaylistsScreenState
}