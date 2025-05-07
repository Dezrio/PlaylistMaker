package com.example.playlistmaker.ui.player.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.player.AudioPlayerCreator
import com.example.playlistmaker.creator.search.HistoryCreator
import com.example.playlistmaker.domain.player.api.interactor.AudioPlayerInteractor
import com.example.playlistmaker.domain.player.models.AudioPlayerState
import com.example.playlistmaker.domain.search.api.interactor.TracksHistoryInteractor
import com.example.playlistmaker.domain.search.models.Track

class AudioPlayerViewModel(
    private val trackId: Int,
    private val audioPlayerInteractor: AudioPlayerInteractor,
    private val tracksHistoryInteractor: TracksHistoryInteractor
) : ViewModel() {
    private var  trackCurrentTime: String = DEFAULT_CUR_TIME

    private val track: Track? = tracksHistoryInteractor.getHistory().find { track -> track.trackId == trackId }
    private val handler = Handler(Looper.getMainLooper())
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

    private val setTrackCurTimeRunnable = object : Runnable {
        override fun run() {
            trackCurrentTime = audioPlayerInteractor.getCurrentPosition()
            audioPlayerLiveData.postValue(AudioPlayerData(
                track,
                AudioPlayerState.STATE_PLAYING,
                trackCurrentTime
            ))
            handler.postDelayed(this, SET_CURRENT_TRACK_TIME_DELAY_MILLIS)
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
        handler.removeCallbacks(setTrackCurTimeRunnable)

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

    private fun playerStartCallback() {
        handler.removeCallbacks(setTrackCurTimeRunnable)
        audioPlayerLiveData.postValue(AudioPlayerData(
            track,
            AudioPlayerState.STATE_PLAYING,
            trackCurrentTime
        ))
        handler.post(setTrackCurTimeRunnable)
    }

    private fun playerPauseCallback() {
        handler.removeCallbacks(setTrackCurTimeRunnable)
        audioPlayerLiveData.postValue(AudioPlayerData(
            track,
            AudioPlayerState.STATE_PAUSED,
            trackCurrentTime
        ))
    }

    private fun playerDefaultCallback() {
        trackCurrentTime = DEFAULT_CUR_TIME
        handler.removeCallbacks(setTrackCurTimeRunnable)
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
        handler.removeCallbacks(setTrackCurTimeRunnable)
        trackCurrentTime = DEFAULT_CUR_TIME
        audioPlayerLiveData.postValue(AudioPlayerData(
            track,
            AudioPlayerState.STATE_PREPARED,
            trackCurrentTime
        ))
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(setTrackCurTimeRunnable)
        audioPlayerInteractor.playerRelease()
    }

    companion object {
        const val DEFAULT_CUR_TIME = "00:00"
        const val SET_CURRENT_TRACK_TIME_DELAY_MILLIS = 500L
        fun getViewModelFactory(trackId: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AudioPlayerViewModel(
                    trackId,
                    AudioPlayerCreator.provideAudioPlayerInteractor(),
                    HistoryCreator.provideTracksHistoryInteractor()
                )
            }
        }
    }
}