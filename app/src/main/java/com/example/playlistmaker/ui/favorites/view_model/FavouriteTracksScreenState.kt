package com.example.playlistmaker.ui.favorites.view_model

import androidx.compose.runtime.Immutable
import com.example.playlistmaker.domain.search.models.Track

@Immutable
sealed interface FavouriteTracksScreenState {
    data object Loading: FavouriteTracksScreenState
    data class Found(val tracks: List<Track>): FavouriteTracksScreenState
    data object NotFound: FavouriteTracksScreenState
}