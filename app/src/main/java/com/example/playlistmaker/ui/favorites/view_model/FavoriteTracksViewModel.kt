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

class FavoriteTracksViewModel
    (private val favoritesInteractor: FavoriteTracksInteractor) : ViewModel()
{

    private val screenStateLiveData = MutableLiveData<ScreenState>()
    fun observeScreenState(): LiveData<ScreenState> = screenStateLiveData

    private val onTrackClickedLiveData = SingleEventLiveData<Track>()
    fun observeOnTrackClickedLiveData(): LiveData<Track> = onTrackClickedLiveData

    private val onTrackClickDebounce: (Track) -> Unit =
        debounce(ON_TRACK_CLICK_DELAY_MILLIS, viewModelScope, false) { track ->
            onTrackClickedLiveData.value = track
        }

    fun updateFavoriteTracks() {
        renderState(ScreenState.Loading)

        viewModelScope.launch {
            favoritesInteractor.getAllTracks()
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }

    private fun renderState(state: ScreenState) {
        screenStateLiveData.postValue(state)
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) renderState(ScreenState.Empty)
        else renderState(ScreenState.Content(tracks))
    }

    fun onTrackClicked(track: Track) {
        onTrackClickDebounce(track)
    }

    companion object {
        private const val ON_TRACK_CLICK_DELAY_MILLIS = 1000L
    }
}