package com.example.playlistmaker.ui.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.search.api.interactor.TracksHistoryInteractor
import com.example.playlistmaker.domain.search.api.interactor.TracksSearchInteractor
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksSearchInteractor: TracksSearchInteractor,
    private val tracksHistoryInteractor: TracksHistoryInteractor
) : ViewModel() {
    private var _oldSeachTextState = MutableStateFlow("")
    val searchTextStateFlow = _oldSeachTextState.asStateFlow()

    private val _eventState = MutableStateFlow<Track?>(null)

    private var tracks: MutableList<Track> = mutableListOf()
    private val tracksHistory: MutableList<Track> = tracksHistoryInteractor.getHistory().toMutableList()

    private val _screenStateFlow = MutableStateFlow<SearchScreenState>(SearchScreenState.DefaultScreenState)
    val screenStateFlow = _screenStateFlow.asStateFlow()

    private val _trackFlow = MutableStateFlow<List<Track>>(tracks)
    val trackFlow = _trackFlow.asStateFlow()

    private val tracksHistoryLiveData = MutableLiveData<List<Track>>(tracksHistory)

    fun getTracksHistoryLiveData(): LiveData<List<Track>> = tracksHistoryLiveData
    val eventState = _eventState.asStateFlow()

    fun onSearchTextChange(newSearchText: String?){
        if (_oldSeachTextState.value == newSearchText)
            return

        _oldSeachTextState.update { newSearchText ?: "" }

        if (_oldSeachTextState.value.isEmpty())
            return

        _screenStateFlow.update { SearchScreenState.TextEnterScreenState(newSearchText ?: "") }
        searchDebounce()
    }

    fun onSearchTextFocusChange(hasFocus: Boolean){
        if (hasFocus && _oldSeachTextState.value.isEmpty() && tracksHistory.isNotEmpty())
            _screenStateFlow.update { SearchScreenState.TrackHistoryScreenState(tracksHistory, _oldSeachTextState.value) }
        else if (!hasFocus && _oldSeachTextState.value.isEmpty())
            _screenStateFlow.update { SearchScreenState.DefaultScreenState }
    }

    private var searchJob: Job? = null

    private fun searchDebounce() {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchTrack(_oldSeachTextState.value)
        }
    }

    private fun searchTrack(trackName: String) {
        searchJob?.cancel()

        _screenStateFlow.update { SearchScreenState.LoadingScreenState }

        searchJob = viewModelScope.launch {
            tracksSearchInteractor
                .searchTracks(trackName)
                .cancellable()
                .collect { foundTracks ->
                    handleSearchResult(foundTracks)
                }
        }
    }

    fun prepareSearch(){
        _eventState.update { null }
    }

    fun refreshSearch(){
        searchTrack(_oldSeachTextState.value)
    }

    fun clearSearchText(){
        _oldSeachTextState.update { "" }
        tracks.clear()
        searchJob?.cancel()
        _screenStateFlow.update { SearchScreenState.DefaultScreenState }
    }

    private fun handleSearchResult(foundTracks: List<Track>?) {
        tracks.clear()

        if (foundTracks == null) {
            _screenStateFlow.update { SearchScreenState.ErrorScreenState(_oldSeachTextState.value) }
        } else{
            if (foundTracks.isNotEmpty()) {
                tracks.addAll(foundTracks)
                _trackFlow.update { tracks }
                _screenStateFlow.update { SearchScreenState.TrackScreenState(tracks, _oldSeachTextState.value) }
            } else {
                _screenStateFlow.update { SearchScreenState.NotFoundScreenState(_oldSeachTextState.value) }
            }
        }
    }

    fun onTrackClick(track: Track) {
        if (!clickDebounce())
            return

        if (!tracksHistory.any { it.trackId == track.trackId })
            saveTrack(track)

        _eventState.update{ track }
    }

    private fun saveTrack(track: Track) {
        tracksHistoryInteractor.updateHistory(track)
        tracksHistory.clear()
        tracksHistory.addAll(tracksHistoryInteractor.getHistory().toMutableList())
        tracksHistoryLiveData.postValue(tracksHistoryInteractor.getHistory().toMutableList())
    }

    private var isClickAllowed = true

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed

        if (isClickAllowed) {
            isClickAllowed = false

            viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }

        return current
    }

    fun clearHistory() {
        tracksHistoryInteractor.clearHistory()
        tracksHistory.clear()
        tracksHistoryLiveData.postValue(tracksHistoryInteractor.getHistory().toMutableList())
    }

    override fun onCleared() {
        searchJob?.cancel()
        super.onCleared()
    }

    companion object {
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}