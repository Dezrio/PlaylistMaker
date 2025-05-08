package com.example.playlistmaker.util

import androidx.lifecycle.MutableLiveData

class EventLiveData<T> : MutableLiveData<T>() {
     override fun setValue(t: T?) {
        super.setValue(t)
        super.setValue(null)
    }
}