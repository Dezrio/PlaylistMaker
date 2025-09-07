package com.example.playlistmaker.domain.player.models

data class AudioPlayerData (
    val audioPlayerState: AudioPlayerState,
    val trackCurTime: String = "00:00")