package com.example.playlistmaker.ui.favorites.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favorites.api.interactor.FavoriteTracksInteractor
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavouriteTracksFragmentViewModel(
    private val favoritesInteractor: FavoriteTracksInteractor
) : ViewModel() {
    private val _screenStateFlow = MutableStateFlow<FavouriteTracksScreenState>(FavouriteTracksScreenState.Loading)
    val screenStateFlow = _screenStateFlow.asStateFlow()

    private val _onTrackClickStateFlow = MutableStateFlow<Track?>(null)
    val onTrackClickStateFlow = _onTrackClickStateFlow.asStateFlow()

    private val onTrackClickDebounce: (Track) -> Unit =
        debounce(CLICK_DEBOUNCE_DELAY, viewModelScope, false) { track ->
            _onTrackClickStateFlow.update { track }
        }

    fun prepareSearch(){
        _onTrackClickStateFlow.update { null }
    }

    fun loadTracks() {
        renderState(FavouriteTracksScreenState.Loading)

        viewModelScope.launch {
            favoritesInteractor.getAllTracks()
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }

    private fun renderState(state: FavouriteTracksScreenState) {
        _screenStateFlow.update { state }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty())
            _screenStateFlow.update { FavouriteTracksScreenState.NotFound }
        else
            _screenStateFlow.update { FavouriteTracksScreenState.Found(tracks) }
    }

    fun onTrackClick(track: Track) {
        onTrackClickDebounce(track)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}