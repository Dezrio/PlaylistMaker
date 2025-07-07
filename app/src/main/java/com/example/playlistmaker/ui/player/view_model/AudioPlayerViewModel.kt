package com.example.playlistmaker.ui.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.player.api.interactor.AudioPlayerInteractor
import com.example.playlistmaker.domain.player.models.AudioPlayerState
import com.example.playlistmaker.domain.search.api.interactor.TracksHistoryInteractor
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val trackId: Int,
    private val audioPlayerInteractor: AudioPlayerInteractor,
    tracksHistoryInteractor: TracksHistoryInteractor
) : ViewModel() {
    private var  trackCurrentTime: String = DEFAULT_CUR_TIME

    private val track: Track? = tracksHistoryInteractor.getHistory().find { track -> track.trackId == trackId }

    private val audioPlayerLiveData = MutableLiveData(AudioPlayerData(
        track,
        AudioPlayerState.STATE_DEFAULT,
    ))

    init {
        track?.previewUrl?.let {
            audioPlayerInteractor.playerPrepare(track.previewUrl,
                { preparedCallback() },
                { completionCallback() })
        }
    }

    fun getAudioPlayerLiveData(): LiveData<AudioPlayerData> = audioPlayerLiveData

    fun playerControl() {
        audioPlayerInteractor.playerControl(
            { playerStartCallback() },
            { playerPauseCallback() },
            { playerDefaultCallback() }
        )
    }

    fun playerPause() {
        playerJob?.cancel()

        if (audioPlayerLiveData.value?.audioPlayerState == AudioPlayerState.STATE_PLAYING)
        {
            audioPlayerInteractor.playerPause { playerPauseCallback() }
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
        trackCurrentTime = DEFAULT_CUR_TIME
        audioPlayerLiveData.postValue(AudioPlayerData(
            track,
            AudioPlayerState.STATE_PREPARED,
            trackCurrentTime
        ))
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

    override fun onCleared() {
        super.onCleared()
        playerJob?.cancel()
        audioPlayerInteractor.playerRelease()
    }

    companion object {
        const val DEFAULT_CUR_TIME = "00:00"
        const val SET_CURRENT_TRACK_TIME_DELAY_MILLIS = 300L
    }
}