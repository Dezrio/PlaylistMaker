package com.example.playlistmaker.ui.medialibrary.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavouriteTracksFragmentViewModel() : ViewModel() {
    private val screenStateLiveData: MutableLiveData<FavouriteTracksScreenState> =
        MutableLiveData(FavouriteTracksScreenState.NotFound)

    fun screenStateObserve(): LiveData<FavouriteTracksScreenState> = screenStateLiveData
}