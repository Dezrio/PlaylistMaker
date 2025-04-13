package com.example.playlistmaker.domain.api.interactor

interface AudioPlayerInteractor {
    fun playerPrepare(
        resourceUrl: String,
        preparedCallback: () -> Unit,
        completionCallback: () -> Unit
    )

    fun playerControl(
        startCallback: () -> Unit,
        pauseCallback: () -> Unit,
        defaultCallback: () -> Unit
    )

    fun playerStart(
        startCallback: () -> Unit
    )

    fun playerPause(
        pauseCallback: () -> Unit
    )

    fun playerRelease()

    fun getCurrentPosition(): String
}