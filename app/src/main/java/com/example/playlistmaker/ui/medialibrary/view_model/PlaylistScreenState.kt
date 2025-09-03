package com.example.playlistmaker.ui.medialibrary.view_model

import com.example.playlistmaker.ui.medialibrary.models.PlaylistInfo

sealed interface PlaylistScreenState {
    data class Found(val info: PlaylistInfo) : PlaylistScreenState
    data object Loading : PlaylistScreenState
}