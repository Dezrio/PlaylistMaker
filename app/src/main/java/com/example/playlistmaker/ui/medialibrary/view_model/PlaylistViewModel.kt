package com.example.playlistmaker.ui.medialibrary.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.medialibrary.api.interactor.PlaylistInteractor
import com.example.playlistmaker.domain.medialibrary.models.Playlist
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.domain.sharing.api.interactor.LinkManagerInteractor
import com.example.playlistmaker.ui.medialibrary.models.PlaylistInfo
import com.example.playlistmaker.util.SingleEventLiveData
import com.example.playlistmaker.util.debounce
import com.example.playlistmaker.utils.ResourcesProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistViewModel(
    private val playlistId: Int,
    private val playlistsInteractor: PlaylistInteractor,
    private val resourcesProvider: ResourcesProvider,
    private val linkManagerInteractor: LinkManagerInteractor
) : ViewModel() {

    private lateinit var playlist: Playlist
    private lateinit var tracks: List<Track>

    private val _screenStateFlow = MutableStateFlow<PlaylistScreenState>(PlaylistScreenState.Loading)
    val screenStateFlow = _screenStateFlow.asStateFlow()

    private val onTrackClickedLiveData = SingleEventLiveData<Track>()
    fun observeOnTrackClickedLiveData(): LiveData<Track> = onTrackClickedLiveData

    private val toastLiveData = SingleEventLiveData<String>()
    fun observeToastLiveData(): LiveData<String> = toastLiveData

    private val deletingPlaylistEventLiveData = SingleEventLiveData<Boolean>()
    fun observeDeletingPlaylistEventLiveData(): LiveData<Boolean> = deletingPlaylistEventLiveData

    init {
        updatePlaylistDetails()
    }

    fun updatePlaylistDetails() {
        viewModelScope.launch {
            playlistsInteractor.getById(playlistId).collect { resultPlaylist ->
                playlist = resultPlaylist
            }

            playlistsInteractor.getTracksByIds(playlist.tracksIds).collect { resultTracks ->
                tracks = resultTracks
                _screenStateFlow.update {
                    PlaylistScreenState.Found(
                        PlaylistInfo(
                            title = playlist.title,
                            description = playlist.description,
                            coverPath = playlist.coverPath,
                            tracks = resultTracks,
                            tracksCountString = resourcesProvider.getQuantityString(R.plurals.track_plurals, resultTracks.size),
                            tracksDuration = resourcesProvider.getQuantityString(R.plurals.time_plurals,  getTracksDuration(resultTracks))
                        )
                    )
                }
            }
        }
    }

    private val onTrackClickedDebounce: (Track) -> Unit =
        debounce(ON_TRACK_CLICK_DELAY_MILLIS, viewModelScope, false) { track ->
            onTrackClickedLiveData.value = track
        }

    private fun getTracksDuration(tracks: List<Track>): Int {
        val tracksDuration = tracks.map { timeToMillisWithDateFormat(it.trackTime) }
        val sumMillis = tracksDuration.sum()

        return (sumMillis / MILLIS_PER_MINUTE).toInt()
    }

    private fun timeToMillisWithDateFormat(timeString: String): Long {
        val format = SimpleDateFormat("mm:ss", Locale.getDefault())
        val date = format.parse(timeString) ?: return 0L
        return date.time
    }

    fun onTrackClicked(track: Track) {
        onTrackClickedDebounce(track)
    }

    fun deleteTrack(trackId: Int) {
        viewModelScope.launch {
            playlistsInteractor.deleteTrack(playlist, trackId)
            updatePlaylistDetails()
        }
    }

    fun sharePlaylist() {
        if (playlist.tracksIds.isEmpty()) toastLiveData.value =
            resourcesProvider.getString(R.string.toast_nothing_tracks_for_sharing_playlist)
        else {
            linkManagerInteractor.shareLink(getMessageForSharing())
        }
    }

    private fun getMessageForSharing(): String {
        val sb = StringBuilder()
        sb.append("${playlist.title}\n")

        if (playlist.description.isNotBlank())
            sb.append("${playlist.description}\n")

        sb.append("${resourcesProvider.getQuantityString(R.plurals.track_plurals, tracks.size)}\n")

        for (i in 0 until playlist.tracksCount) {
            sb.append(
                "${i + 1}. ${tracks[i].artistName} - ${tracks[i].trackName} (${tracks[i].trackTime})\n"
            )
        }

        return sb.toString()
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistsInteractor.delete(playlist)
            deletingPlaylistEventLiveData.postValue(true)
        }
    }

    companion object {
        private const val MILLIS_PER_MINUTE = 60_000
        private const val ON_TRACK_CLICK_DELAY_MILLIS = 1000L
    }
}