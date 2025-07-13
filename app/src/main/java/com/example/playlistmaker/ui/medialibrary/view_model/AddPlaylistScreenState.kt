package com.example.playlistmaker.ui.medialibrary.view_model

import com.example.playlistmaker.domain.medialibrary.models.Playlist

sealed interface AddPlaylistScreenState {
    data object NotFound: AddPlaylistScreenState
    data class Found(val url: String): AddPlaylistScreenState
}