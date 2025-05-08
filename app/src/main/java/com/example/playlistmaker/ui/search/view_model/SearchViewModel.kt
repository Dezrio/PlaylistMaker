package com.example.playlistmaker.ui.search.view_model

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.search.HistoryCreator
import com.example.playlistmaker.creator.search.SearchCreator
import com.example.playlistmaker.domain.search.api.interactor.TracksHistoryInteractor
import com.example.playlistmaker.domain.search.api.interactor.TracksSearchInteractor
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.util.EventLiveData

class SearchViewModel(
    private val tracksSearchInteractor: TracksSearchInteractor,
    private val tracksHistoryInteractor: TracksHistoryInteractor
) : ViewModel() {
    private var oldSeachText: String = ""
    private val eventLiveData = EventLiveData<Int?>()

    init {
        eventLiveData.postValue(null)
    }

    private var tracks: MutableList<Track> = mutableListOf()
    private val tracksHistory: MutableList<Track> = tracksHistoryInteractor.getHistory().toMutableList()

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchTrack(oldSeachText) }

    private val screenStateLiveData = MutableLiveData<ScreenState>(ScreenState.DefaultScreenState)
    private val trackLiveData = MutableLiveData<List<Track>>(tracks)
    private val tracksHistoryLiveData = MutableLiveData<List<Track>>(tracksHistory)

    fun getScreenSateLiveData(): LiveData<ScreenState> = screenStateLiveData
    fun getTrackLiveData(): LiveData<List<Track>> = trackLiveData
    fun getTracksHistoryLiveData(): LiveData<List<Track>> = tracksHistoryLiveData
    fun getEventLiveData(): LiveData<Int?> = eventLiveData

    fun onSearchTextChange(newSearchText: String?){
        if (oldSeachText == newSearchText)
            return

        oldSeachText = newSearchText ?: ""

        if (oldSeachText.isEmpty())
            return

        screenStateLiveData.postValue(ScreenState.TextEnterScreenState)
        searchDebounce()
    }

    fun onSearchTextFocusChange(hasFocus: Boolean){
        if (hasFocus && oldSeachText.isEmpty() && tracksHistory.isNotEmpty())
            screenStateLiveData.postValue(ScreenState.TrackHistoryScreenState)
        else if (!hasFocus && oldSeachText.isEmpty())
            screenStateLiveData.postValue(ScreenState.DefaultScreenState)
    }

    private fun searchDebounce() {
        handler.removeCallbacksAndMessages(SEARCH_TOKEN)

        handler.postAtTime(
            searchRunnable,
            SEARCH_TOKEN,
            SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        )
    }

    private fun searchTrack(trackName: String) {
        screenStateLiveData.postValue(ScreenState.LoadingScreenState)
        tracksSearchInteractor.searchTracks(trackName,
            object : TracksSearchInteractor.TrackConsumer {
                override fun consume(foundTracks: List<Track>?) {
                    handleSearchResult(foundTracks)
                }
            })
    }

    fun refreshSearch(){
        searchTrack(oldSeachText)
    }

    fun clearSearchText(){
        tracks.clear()
        handler.removeCallbacksAndMessages(SEARCH_TOKEN)
        screenStateLiveData.postValue(ScreenState.DefaultScreenState)
    }

    private fun handleSearchResult(foundTracks: List<Track>?) {
        tracks.clear()

        if (foundTracks == null) {
            screenStateLiveData.postValue(ScreenState.ErrorScreenState)
        } else{
            if (foundTracks.isNotEmpty()) {
                tracks.addAll(foundTracks)
                trackLiveData.postValue(tracks)
                screenStateLiveData.postValue(ScreenState.TrackScreenState)
            } else {
                screenStateLiveData.postValue(ScreenState.NotFoundScreenState)
            }
        }
    }

    fun onTrackClick(track: Track) {
        if (!clickDebounce())
            return

        if (!tracksHistory.any { it.trackId == track.trackId })
            saveTrack(track)

        eventLiveData.postValue(track.trackId)
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
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }

        return current
    }

    fun clearHistory() {
        tracksHistoryInteractor.clearHistory()
        tracksHistory.clear()
        tracksHistoryLiveData.postValue(tracksHistoryInteractor.getHistory().toMutableList())
    }

    companion object {
        private val SEARCH_TOKEN = Any()
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(
                    SearchCreator.provideTracksSearchInteractor(),
                    HistoryCreator.provideTracksHistoryInteractor()
                )
            }
        }
    }
}