package com.example.playlistmaker.ui.medialibrary.view_model

sealed interface AddPlaylistScreenState {
    data object NotFound: AddPlaylistScreenState
    data class Found(val url: String): AddPlaylistScreenState
}