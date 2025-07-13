package com.example.playlistmaker.ui.medialibrary.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.medialibrary.api.interactor.PlaylistInteractor
import com.example.playlistmaker.domain.medialibrary.models.Playlist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {
    private val _screenStateFlow =
        MutableStateFlow<PlaylistsScreenState>(PlaylistsScreenState.Loading)

    val screenStateFlow = _screenStateFlow.asStateFlow()

    fun updatePlaylists() {
        renderState(PlaylistsScreenState.Loading)

        viewModelScope.launch {
            playlistInteractor.getAll()
                .collect { playlists ->
                    if (playlists.isEmpty()) {
                        renderState(PlaylistsScreenState.NotFound)
                    } else {
                        renderState(PlaylistsScreenState.Found(playlists))
                    }
                }
        }
    }

    private fun renderState(state: PlaylistsScreenState) {
        _screenStateFlow.update {
            state
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.delete(playlist)
            updatePlaylists()
        }
    }
}