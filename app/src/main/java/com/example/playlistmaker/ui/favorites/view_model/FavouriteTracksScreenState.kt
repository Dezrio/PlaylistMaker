package com.example.playlistmaker.ui.favorites.view_model

import com.example.playlistmaker.domain.search.models.Track

sealed interface FavouriteTracksScreenState {
    data object Loading: FavouriteTracksScreenState
    data class Found(val tracks: List<Track>): FavouriteTracksScreenState
    data object NotFound: FavouriteTracksScreenState
}