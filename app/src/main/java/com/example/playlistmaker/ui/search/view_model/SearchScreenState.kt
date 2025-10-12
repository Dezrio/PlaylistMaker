package com.example.playlistmaker.ui.search.view_model

import androidx.compose.runtime.Immutable
import com.example.playlistmaker.domain.search.models.Track

@Immutable
sealed class SearchScreenState(val searchText: String = "") {
    data object DefaultScreenState : SearchScreenState()
    data class TextEnterScreenState(val text: String?) : SearchScreenState()
    data class TrackScreenState(val tracks: List<Track>) : SearchScreenState()
    data class TrackHistoryScreenState(val tracks: List<Track>) : SearchScreenState()
    data object LoadingScreenState : SearchScreenState()
    data object NotFoundScreenState : SearchScreenState()
    data object ErrorScreenState : SearchScreenState()
}