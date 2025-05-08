package com.example.playlistmaker.ui.player.view_model

import com.example.playlistmaker.domain.player.models.AudioPlayerState
import com.example.playlistmaker.domain.search.models.Track

data class AudioPlayerData (
    val track: Track?,
    val audioPlayerState: AudioPlayerState,
    val trackCurTime: String = "00:00")