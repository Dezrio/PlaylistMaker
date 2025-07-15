package com.example.playlistmaker.ui.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favorites.api.interactor.FavoriteTracksInteractor
import com.example.playlistmaker.domain.medialibrary.api.interactor.PlaylistInteractor
import com.example.playlistmaker.domain.medialibrary.models.Playlist
import com.example.playlistmaker.domain.player.api.interactor.AudioPlayerInteractor
import com.example.playlistmaker.domain.player.models.AudioPlayerState
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.ui.medialibrary.view_model.AddPlaylistResultScreenState
import com.example.playlistmaker.ui.medialibrary.view_model.PlaylistsScreenState
import com.example.playlistmaker.util.SingleEventLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AudioPlayerViewModel(
    private var track: Track,
    private val audioPlayerInteractor: AudioPlayerInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {
    private var trackCurrentTime: String = DEFAULT_CUR_TIME

    private val audioPlayerLiveData = MutableLiveData(AudioPlayerData(
        track,
        AudioPlayerState.STATE_DEFAULT,
    ))

    private val addTrackLiveData = SingleEventLiveData<AddPlaylistResultScreenState>()

    private val playlistsLiveData = MutableLiveData<PlaylistsScreenState>()

    init {
        track.previewUrl?.let { url ->
            audioPlayerInteractor.preparePlayer(url,
                { preparedCallback() },
                { completionCallback() })
        }
    }

    fun getAudioPlayerLiveData(): LiveData<AudioPlayerData> = audioPlayerLiveData
    fun getAddTrackLiveData(): LiveData<AddPlaylistResultScreenState> = addTrackLiveData
    fun getPlaylistsLiveData(): LiveData<PlaylistsScreenState> = playlistsLiveData

    fun playerControl() {
        audioPlayerInteractor.controlPlayer(
            { playerStartCallback() },
            { playerPauseCallback() },
            { playerDefaultCallback() }
        )
    }

    fun playerPause() {
        playerJob?.cancel()

        if (audioPlayerLiveData.value?.audioPlayerState == AudioPlayerState.STATE_PLAYING)
        {
            audioPlayerInteractor.pausePlayer { playerPauseCallback() }
            audioPlayerLiveData.postValue(AudioPlayerData(
                track,
                AudioPlayerState.STATE_PAUSED,
                trackCurrentTime
            ))
        }
    }

    private var playerJob: Job? = null

    private fun playerStartCallback() {
        playerJob?.cancel()
        audioPlayerLiveData.postValue(AudioPlayerData(
            track,
            AudioPlayerState.STATE_PLAYING,
            trackCurrentTime
        ))

        playerJob = viewModelScope.launch {
            do {
                delay(SET_CURRENT_TRACK_TIME_DELAY_MILLIS)

                trackCurrentTime = audioPlayerInteractor.getCurrentPosition()
                audioPlayerLiveData.postValue(AudioPlayerData(
                    track,
                    AudioPlayerState.STATE_PLAYING,
                    trackCurrentTime
                ))
            } while((audioPlayerLiveData.value?.audioPlayerState ?: AudioPlayerState.STATE_DEFAULT) == AudioPlayerState.STATE_PLAYING)
        }
    }

    private fun playerPauseCallback() {
        playerJob?.cancel()
        audioPlayerLiveData.postValue(AudioPlayerData(
            track,
            AudioPlayerState.STATE_PAUSED,
            trackCurrentTime
        ))
    }

    private fun playerDefaultCallback() {
        trackCurrentTime = DEFAULT_CUR_TIME
        playerJob?.cancel()
        audioPlayerLiveData.postValue(AudioPlayerData(
            track,
            AudioPlayerState.STATE_DEFAULT,
            trackCurrentTime
        ))
    }

    private fun preparedCallback() {
        viewModelScope.launch {
            favoriteTracksInteractor.isFavorite(trackId = track.trackId)
                .flowOn(Dispatchers.IO)
                .collect { isFavorite ->
                    withContext(Dispatchers.Main) {
                        track.isFavorite = isFavorite
                        trackCurrentTime = DEFAULT_CUR_TIME
                        audioPlayerLiveData.postValue(AudioPlayerData(
                            track,
                            AudioPlayerState.STATE_PREPARED,
                            trackCurrentTime
                        ))
                    }
                }
        }
    }

    private fun completionCallback() {
        playerJob?.cancel()
        trackCurrentTime = DEFAULT_CUR_TIME
        audioPlayerLiveData.postValue(AudioPlayerData(
            track,
            AudioPlayerState.STATE_PREPARED,
            trackCurrentTime
        ))
    }

    fun onFavoriteClick() {
        viewModelScope.launch(Dispatchers.IO) {
            if (track.isFavorite)
                favoriteTracksInteractor.deleteTrack(track)
            else
                favoriteTracksInteractor.saveTrack(track)

            withContext(Dispatchers.Main){
                track.isFavorite = !track.isFavorite

                audioPlayerLiveData.postValue(
                    audioPlayerLiveData.value?.copy(
                        track = track
                ))
            }
        }
    }

    fun btnAddTrackToPlaylistClicked() {
        playlistsLiveData.postValue(PlaylistsScreenState.Loading)

        viewModelScope.launch {
            playlistInteractor.getAll().collect { playlists ->
                if (playlists.isEmpty())
                    playlistsLiveData.postValue(PlaylistsScreenState.NotFound)
                else
                    playlistsLiveData.postValue(PlaylistsScreenState.Found(playlists))
            }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        if (playlist.tracksIds.contains(track.trackId))
            addTrackLiveData.postValue(AddPlaylistResultScreenState.AlreadyExists(playlist.title))
        else {
            viewModelScope.launch {
                playlistInteractor.addNewTrack(playlist, track)
                addTrackLiveData.postValue(AddPlaylistResultScreenState.Created(playlist.title, playlist.coverPath, null))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerJob?.cancel()
        audioPlayerInteractor.releasePlayer()
    }

    companion object {
        const val DEFAULT_CUR_TIME = "00:00"
        const val SET_CURRENT_TRACK_TIME_DELAY_MILLIS = 300L
    }
}