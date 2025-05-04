package com.example.playlistmaker.ui.player.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.App.Companion.TRACK_KEY
import com.example.playlistmaker.creator.player.AudioPlayerCreator
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.google.gson.Gson
import com.example.playlistmaker.domain.search.models.Track

class AudioPlayerActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private val audioPlayerInteractorImpl = AudioPlayerCreator.provideAudioPlayerInteractor()

    private val setTrackCurTimeRunnable = object : Runnable {
        override fun run() {
            binding.tvTrackCurrentTime.text = audioPlayerInteractorImpl.getCurrentPosition()
            handler.postDelayed(this, SET_CURRENT_TRACK_TIME_DELAY_MILLIS)
        }
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

        val extras = getIntent().getExtras();
        val track: Track? = extras?.let { Gson().fromJson(extras.getString(TRACK_KEY), Track::class.java) }

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
            binding.tvAlbum.visibility = View.VISIBLE
            binding.tvAlbumText.visibility = View.VISIBLE
        } else {
            binding.tvAlbum.visibility = View.GONE
            binding.tvAlbumText.visibility = View.GONE
        }

        if (track?.previewUrl != null) {
            preparePlayer(track.previewUrl)
        }

        binding.ibtnPlay.setOnClickListener {
            playerControl()
        }
    }

    override fun onPause() {
        super.onPause()
        audioPlayerInteractorImpl.playerPause({
            handler.removeCallbacks(setTrackCurTimeRunnable)
            binding.ibtnPlay.setImageResource(R.drawable.ic_play)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayerInteractorImpl.playerRelease()
    }

    private fun preparePlayer(trackUrl: String) {
        audioPlayerInteractorImpl.playerPrepare(
            trackUrl,
            {
                binding.ibtnPlay.isEnabled = true
            },
            {
                binding.ibtnPlay.setImageResource(R.drawable.ic_play)
                handler.removeCallbacks(setTrackCurTimeRunnable)
                binding.tvTrackCurrentTime.text  = getString(R.string.track_time_placeholder)
            }
        )
    }

    private fun playerControl() {
        audioPlayerInteractorImpl.playerControl(
            {
                handler.post(setTrackCurTimeRunnable)
                binding.ibtnPlay.setImageResource(R.drawable.ic_pause)
            },
            {
                handler.removeCallbacks(setTrackCurTimeRunnable)
                binding.ibtnPlay.setImageResource(R.drawable.ic_play)
            },
            {
                binding.tvAlbum.visibility = View.GONE
                binding.tvAlbumText.visibility = View.GONE
            }
        )
    }

    private companion object {
        const val SET_CURRENT_TRACK_TIME_DELAY_MILLIS = 500L
    }
}