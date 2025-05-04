package com.example.playlistmaker.creator.player

import android.media.MediaPlayer
import com.example.playlistmaker.domain.player.impl.AudioPlayerInteractorImpl

object AudioPlayerCreator {
    fun provideAudioPlayerInteractor(): AudioPlayerInteractorImpl {
        return AudioPlayerInteractorImpl(MediaPlayer())
    }
}