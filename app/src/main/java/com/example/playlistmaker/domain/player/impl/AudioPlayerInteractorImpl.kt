package com.example.playlistmaker.domain.player.impl

import android.media.MediaPlayer
import com.example.playlistmaker.domain.player.api.interactor.AudioPlayerInteractor
import com.example.playlistmaker.domain.player.models.AudioPlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerInteractorImpl(private val player: MediaPlayer) : AudioPlayerInteractor {
    private var playerState = AudioPlayerState.STATE_DEFAULT

    override fun playerPrepare(
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

    override fun playerControl(
        startCallback: () -> Unit,
        pauseCallback: () -> Unit,
        defaultCallback: () -> Unit
    ) {
        when (playerState) {
            AudioPlayerState.STATE_PREPARED, AudioPlayerState.STATE_PAUSED -> {
                playerStart(startCallback)
            }
            AudioPlayerState.STATE_PLAYING -> {
                playerPause(pauseCallback)
            }
            else -> {
                defaultCallback()
            }

        }
    }

    override fun playerStart(startCallback: () -> Unit) {
        player.start()
        startCallback()
        playerState = AudioPlayerState.STATE_PLAYING
    }

    override fun playerPause(pauseCallback: () -> Unit) {
        player.pause()
        pauseCallback()
        playerState = AudioPlayerState.STATE_PAUSED
    }

    override fun playerRelease() {
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