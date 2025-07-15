package com.example.playlistmaker.ui.medialibrary.view_model

import java.io.File

interface AddPlaylistResultScreenState {
    data class Created(
        val playlistTitle: String,
        val coverUri: String,
        val filePath: File?
    ) : AddPlaylistResultScreenState

    data class AlreadyExists(val playlistTitle: String) : AddPlaylistResultScreenState
}