package com.example.playlistmaker.domain.player.api.interactor

interface AudioPlayerInteractor {
    fun preparePlayer(
        resourceUrl: String,
        preparedCallback: () -> Unit,
        completionCallback: () -> Unit
    )

    fun controlPlayer(
        startCallback: () -> Unit,
        pauseCallback: () -> Unit,
        defaultCallback: () -> Unit
    )

    fun startPlayer(
        startCallback: () -> Unit
    )

    fun pausePlayer(
        pauseCallback: () -> Unit
    )

    fun releasePlayer()

    fun getCurrentPosition(): String
}