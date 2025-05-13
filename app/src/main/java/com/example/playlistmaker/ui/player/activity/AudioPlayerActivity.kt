package com.example.playlistmaker.ui.player.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.domain.player.models.AudioPlayerState
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.ui.player.view_model.AudioPlayerViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class AudioPlayerActivity : AppCompatActivity() {

    private var trackId: Int = -1
    private val args: AudioPlayerActivityArgs by navArgs()
    private val viewModel: AudioPlayerViewModel by lazy {
        getViewModel { parametersOf(trackId) }
    }

    private lateinit var binding: ActivityAudioPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.playerHeader.setOnClickListener {
            finish()
        }

        trackId = args.trackId

        viewModel.getAudioPlayerLiveData().observe(this) { data ->
            when (data.audioPlayerState) {
                AudioPlayerState.STATE_DEFAULT -> {
                    showDefaultState(data.track, data.trackCurTime)
                }

                AudioPlayerState.STATE_PREPARED -> {
                    showPreparedState(data.trackCurTime)
                }

                AudioPlayerState.STATE_PLAYING -> {
                    showPlayingState(data.trackCurTime)
                }

                AudioPlayerState.STATE_PAUSED -> {
                    showPauseState(data.trackCurTime)
                }
            }
        }

        binding.ibtnPlay.setOnClickListener {
            viewModel.playerControl()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.playerPause()
    }

    private fun showDefaultState(track: Track?, trackCurTime: String){
        binding.tvAlbum.isVisible = false
        binding.tvAlbumText.isVisible = false
        binding.tvTrackCurrentTime.text  = trackCurTime
        trackInit(track)
    }

    private fun showPreparedState(trackCurTime: String){
        binding.ibtnPlay.isEnabled = true
        binding.tvTrackCurrentTime.text  = trackCurTime
        binding.ibtnPlay.setImageResource(R.drawable.ic_play)
    }

    private fun showPlayingState(trackCurTime: String){
        binding.tvTrackCurrentTime.text  = trackCurTime
        binding.ibtnPlay.setImageResource(R.drawable.ic_pause)
    }

    private fun showPauseState(trackCurTime: String){
        binding.tvTrackCurrentTime.text  = trackCurTime
        binding.ibtnPlay.setImageResource(R.drawable.ic_play)
    }

    private fun trackInit(track: Track?) {
        Glide.with(this)
            .load(track?.artworkUrl512  ?: "")
            .centerInside()
            .transform(RoundedCorners(8))
            .placeholder(R.drawable.player_placeholder)
            .into(binding.ivPlayerCover)

        binding.tvTrackName.text = track?.trackName ?: getString(R.string.not_found_search_text)
        binding.tvArtistName.text = track?.artistName ?: getString(R.string.not_found_search_text)
        binding.tvTrackCurrentTime.text = getString(R.string.track_time_placeholder)
        binding.tvTrackDurationText.text = track?.trackTime ?: getString(R.string.not_found_search_text)
        binding.tvYearText.text = track?.releaseDate ?: getString(R.string.not_found_search_text)
        binding.tvGenreText.text = track?.primaryGenreName ?: getString(R.string.not_found_search_text)
        binding.tvCountryText.text = track?.country ?: getString(R.string.not_found_search_text)

        if (track?.collectionName != null) {
            binding.tvAlbumText.text = track.collectionName
            binding.tvAlbum.isVisible = true
            binding.tvAlbumText.isVisible = true
        } else {
            binding.tvAlbum.isVisible = false
            binding.tvAlbumText.isVisible = false
        }
    }
}