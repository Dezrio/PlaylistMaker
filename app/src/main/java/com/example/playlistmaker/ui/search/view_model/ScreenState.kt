package com.example.playlistmaker.ui.search.view_model

sealed class ScreenState {
    data object DefaultScreenState : ScreenState()
    data object TextEnterScreenState : ScreenState()
    data object TrackScreenState : ScreenState()
    data object TrackHistoryScreenState : ScreenState()
    data object LoadingScreenState : ScreenState()
    data object NotFoundScreenState : ScreenState()
    data object ErrorScreenState : ScreenState()
    data object AudioPlayerState : ScreenState()
}