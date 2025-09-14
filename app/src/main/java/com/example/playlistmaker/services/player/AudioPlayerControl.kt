package com.example.playlistmaker.services.player

import com.example.playlistmaker.domain.player.models.AudioPlayerData
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerControl {
    fun getAudioPlayerData(): StateFlow<AudioPlayerData>

    fun startPlayer()

    fun pausePlayer()

    fun startForeground()

    fun stopForeground()
}