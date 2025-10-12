package com.example.playlistmaker.ui.medialibrary.view_model

import androidx.compose.runtime.Immutable
import com.example.playlistmaker.domain.medialibrary.models.Playlist

@Immutable
sealed interface PlaylistsScreenState {
    data object Loading: PlaylistsScreenState
    data class Found(val playlists: List<Playlist>): PlaylistsScreenState
    data object NotFound: PlaylistsScreenState
}