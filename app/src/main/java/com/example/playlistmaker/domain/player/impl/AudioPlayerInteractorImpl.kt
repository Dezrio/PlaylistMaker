package com.example.playlistmaker.domain.player.impl

import android.media.MediaPlayer
import com.example.playlistmaker.domain.player.api.interactor.AudioPlayerInteractor
import com.example.playlistmaker.domain.player.models.AudioPlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerInteractorImpl(private val player: MediaPlayer) : AudioPlayerInteractor {
    private var playerState = AudioPlayerState.STATE_DEFAULT

    override fun preparePlayer(
        resourceUrl: String,
        preparedCallback: () -> Unit,
        completionCallback: () -> Unit
    ) {
        player.setDataSource(resourceUrl)
        player.prepareAsync()
        player.setOnPreparedListener {
            preparedCallback()
            playerState = AudioPlayerState.STATE_PREPARED
        }
        player.setOnCompletionListener {
            completionCallback()
            playerState = AudioPlayerState.STATE_PREPARED
        }
    }

    override fun controlPlayer(
        startCallback: () -> Unit,
        pauseCallback: () -> Unit,
        defaultCallback: () -> Unit
    ) {
        when (playerState) {
            AudioPlayerState.STATE_PREPARED, AudioPlayerState.STATE_PAUSED -> {
                startPlayer(startCallback)
            }
            AudioPlayerState.STATE_PLAYING -> {
                pausePlayer(pauseCallback)
            }
            else -> {
                defaultCallback()
            }

        }
    }

    override fun startPlayer(startCallback: () -> Unit) {
        player.start()
        startCallback()
        playerState = AudioPlayerState.STATE_PLAYING
    }

    override fun pausePlayer(pauseCallback: () -> Unit) {
        player.pause()
        pauseCallback()
        playerState = AudioPlayerState.STATE_PAUSED
    }

    override fun releasePlayer() {
        player.release()
    }

    override fun getCurrentPosition(): String {
        return SimpleDateFormat(TRACK_TIME_PATTERN, Locale.getDefault())
            .format(player.currentPosition)
    }

    private companion object {
        const val TRACK_TIME_PATTERN = "mm:ss"
    }
}