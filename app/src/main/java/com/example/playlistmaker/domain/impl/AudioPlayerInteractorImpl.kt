package com.example.playlistmaker.domain.impl

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.interactor.AudioPlayerInteractor
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerInteractorImpl(private val player: MediaPlayer) : AudioPlayerInteractor {
    private var playerState = STATE_DEFAULT

    override fun playerPrepare(
        resourceUrl: String,
        preparedCallback: () -> Unit,
        completionCallback: () -> Unit
    ) {
        player.setDataSource(resourceUrl)
        player.prepareAsync()
        player.setOnPreparedListener {
            preparedCallback()
            playerState = STATE_PREPARED
        }
        player.setOnCompletionListener {
            completionCallback()
            playerState = STATE_PREPARED
        }
    }

    override fun playerControl(
        startCallback: () -> Unit,
        pauseCallback: () -> Unit,
        defaultCallback: () -> Unit
    ) {
        when (playerState) {
            STATE_PREPARED, STATE_PAUSED -> {
                playerStart(startCallback)
            }
            STATE_PLAYING -> {
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
        playerState = STATE_PLAYING
    }

    override fun playerPause(pauseCallback: () -> Unit) {
        player.pause()
        pauseCallback()
        playerState = STATE_PAUSED
    }

    override fun playerRelease() {
        player.release()
    }

    override fun getCurrentPosition(): String {
        return SimpleDateFormat(TRACK_TIME_PATTERN, Locale.getDefault())
            .format(player.currentPosition)
    }

    private companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val TRACK_TIME_PATTERN = "mm:ss"
    }
}