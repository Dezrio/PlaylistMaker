package com.example.playlistmaker.ui.favorites.view_model

import com.example.playlistmaker.domain.search.models.Track

sealed class ScreenState {
    data object Loading: ScreenState()
    data class Content(val tracks: List<Track>): ScreenState()
    data object Empty: ScreenState()
}