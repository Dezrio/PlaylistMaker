package com.example.playlistmaker.ui.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favorites.api.interactor.FavoriteTracksInteractor
import com.example.playlistmaker.domain.medialibrary.api.interactor.PlaylistInteractor
import com.example.playlistmaker.domain.medialibrary.models.Playlist
import com.example.playlistmaker.domain.player.models.AudioPlayerState
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.services.player.AudioPlayerControl
import com.example.playlistmaker.ui.medialibrary.view_model.AddPlaylistResultScreenState
import com.example.playlistmaker.ui.medialibrary.view_model.PlaylistsScreenState
import com.example.playlistmaker.util.SingleEventLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AudioPlayerViewModel(
    private var track: Track,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {
    private val audioPlayerLiveData = MutableLiveData(AudioPlayerScreenData(
        track,
        AudioPlayerState.STATE_DEFAULT
    ))

    private val addTrackLiveData = SingleEventLiveData<AddPlaylistResultScreenState>()

    private val playlistsLiveData = MutableLiveData<PlaylistsScreenState>()

    private var audioPlayerControl: AudioPlayerControl? = null
    private var audioPlayerControlJob: Job? = null

    fun setAudioPlayerControl(audioPlayerControl: AudioPlayerControl) {
        this.audioPlayerControl = audioPlayerControl
        this.audioPlayerControlJob = viewModelScope.launch {
            audioPlayerControl.getAudioPlayerData().collect { data ->
                audioPlayerLiveData.postValue(AudioPlayerScreenData(
                    track,
                    data.audioPlayerState,
                    data.trackCurTime
                ))
            }
        }
    }

    fun removeAudioPlayerControl() {
        audioPlayerControlJob?.cancel()
        audioPlayerControl = null
    }

    fun getAudioPlayerLiveData(): LiveData<AudioPlayerScreenData> = audioPlayerLiveData
    fun getAddTrackLiveData(): LiveData<AddPlaylistResultScreenState> = addTrackLiveData
    fun getPlaylistsLiveData(): LiveData<PlaylistsScreenState> = playlistsLiveData

    fun playerControl() {
        when (audioPlayerLiveData.value?.audioPlayerState) {
            AudioPlayerState.STATE_PREPARED, AudioPlayerState.STATE_PAUSED -> {
                audioPlayerControl?.startPlayer()
            }
            AudioPlayerState.STATE_PLAYING -> {
                audioPlayerControl?.pausePlayer()
            }
            else -> {}
        }
    }

    fun playerPause() {
        if (audioPlayerLiveData.value?.audioPlayerState == AudioPlayerState.STATE_PLAYING)
            audioPlayerControl?.startForeground()
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
}