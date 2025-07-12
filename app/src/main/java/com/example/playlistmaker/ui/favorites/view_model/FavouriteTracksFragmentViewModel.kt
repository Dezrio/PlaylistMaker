package com.example.playlistmaker.ui.favorites.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favorites.api.interactor.FavoriteTracksInteractor
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.util.SingleEventLiveData
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.launch

class FavouriteTracksFragmentViewModel(
    private val favoritesInteractor: FavoriteTracksInteractor
) : ViewModel() {
    private val screenStateLiveData = MutableLiveData<FavouriteTracksScreenState>()
    fun observeScreenState(): LiveData<FavouriteTracksScreenState> = screenStateLiveData

    private val onTrackClickLiveData = SingleEventLiveData<Track>()
    fun observeOnTrackClickLiveData(): LiveData<Track> = onTrackClickLiveData

    private val onTrackClickDebounce: (Track) -> Unit =
        debounce(CLICK_DEBOUNCE_DELAY, viewModelScope, false) { track ->
            onTrackClickLiveData.value = track
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
        screenStateLiveData.postValue(state)
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty())
            renderState(FavouriteTracksScreenState.NotFound)
        else
            renderState(FavouriteTracksScreenState.Found(tracks))
    }

    fun onTrackClick(track: Track) {
        onTrackClickDebounce(track)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}