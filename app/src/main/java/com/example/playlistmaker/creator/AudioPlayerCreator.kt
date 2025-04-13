package com.example.playlistmaker.creator

import android.media.MediaPlayer
import com.example.playlistmaker.domain.impl.AudioPlayerInteractorImpl

object AudioPlayerCreator {
    fun provideAudioPlayerInteractor(): AudioPlayerInteractorImpl {
        return AudioPlayerInteractorImpl(MediaPlayer())
    }
}