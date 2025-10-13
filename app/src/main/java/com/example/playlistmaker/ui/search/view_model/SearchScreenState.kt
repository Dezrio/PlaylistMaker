package com.example.playlistmaker.ui.search.view_model

import androidx.compose.runtime.Immutable
import com.example.playlistmaker.domain.search.models.Track

@Immutable
sealed class SearchScreenState(val searchText: String = "") {
    data object DefaultScreenState : SearchScreenState()
    data class TextEnterScreenState(val text: String) : SearchScreenState(text)
    data class TrackScreenState(val tracks: List<Track>, val text: String) : SearchScreenState(text)
    data class TrackHistoryScreenState(val tracks: List<Track>, val text: String) : SearchScreenState(text)
    data object LoadingScreenState : SearchScreenState()
    data class NotFoundScreenState(val text: String) : SearchScreenState(text)
    data class ErrorScreenState(val text: String) : SearchScreenState(text)
}