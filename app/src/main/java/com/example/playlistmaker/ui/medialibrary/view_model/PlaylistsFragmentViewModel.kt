package com.example.playlistmaker.ui.medialibrary.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistsFragmentViewModel() : ViewModel() {
    private val screenStateLiveData: MutableLiveData<PlaylistsScreenState> =
        MutableLiveData(PlaylistsScreenState.NotFound)

    fun screenStateObserve(): LiveData<PlaylistsScreenState> = screenStateLiveData
}