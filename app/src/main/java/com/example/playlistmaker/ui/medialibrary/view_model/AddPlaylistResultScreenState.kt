package com.example.playlistmaker.ui.medialibrary.view_model

import java.io.File

interface AddPlaylistResultScreenState {
    data class Created(
        val playlistTitle: String,
        val coverUri: String,
        val filePath: File?
    ) : AddPlaylistResultScreenState

    data class Found(
        val playlistTitle: String,
        val description: String,
        val coverUri: String
    ) : AddPlaylistResultScreenState

    data class Updated(
        val playlistTitle: String,
        val oldTitle: String,
        val coverUri: String,
        val filePath: File?,
        val needUpdateCover: Boolean
    ) : AddPlaylistResultScreenState

    data class AlreadyExists(val playlistTitle: String) : AddPlaylistResultScreenState
}