package com.example.playlistmaker

import android.media.MediaPlayer
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
import com.example.playlistmaker.App.Companion.TRACK_KEY
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.google.gson.Gson
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private val player = MediaPlayer()
    private var playerState = STATE_DEFAULT

    private val setTrackCurTimeRunnable = object : Runnable {
        override fun run() {
            binding.tvTrackCurrentTime.text = SimpleDateFormat(TRACK_TIME_PATTERN, Locale.getDefault())
                .format(player.currentPosition)
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
            .load(track?.getCoverArtwork() ?: "")
            .centerInside()
            .transform(RoundedCorners(8))
            .placeholder(R.drawable.player_placeholder)
            .into(binding.ivPlayerCover)

        binding.tvTrackName.text = track?.trackName ?: getString(R.string.not_found_search_text)
        binding.tvArtistName.text = track?.artistName ?: getString(R.string.not_found_search_text)
        binding.tvTrackCurrentTime.text = getString(R.string.track_time_placeholder)
        binding.tvTrackDurationText.text = track?.getTrackTime() ?: getString(R.string.not_found_search_text)
        binding.tvYearText.text = track?.getTrackYear() ?: getString(R.string.not_found_search_text)
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
            if (playerState != STATE_DEFAULT) {
                playerControl()
            } else {
                binding.tvAlbum.visibility = View.GONE
                binding.tvAlbumText.visibility = View.GONE
            }
        }
    }

    override fun onPause() {
        super.onPause()
        playerPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    private fun preparePlayer(trackUrl: String) {
        player.setDataSource(trackUrl)
        player.prepareAsync()
        player.setOnPreparedListener {
            binding.ibtnPlay.isEnabled = true
            playerState = STATE_PREPARED
        }
        player.setOnCompletionListener {
            binding.ibtnPlay.setImageResource(R.drawable.ic_play)
            handler.removeCallbacks(setTrackCurTimeRunnable)
            binding.tvTrackCurrentTime.text  = getString(R.string.track_time_placeholder)
            playerState = STATE_PREPARED
        }
    }

    private fun playerControl() {
        when (playerState) {
            STATE_PLAYING -> playerPause()
            STATE_PREPARED, STATE_PAUSED -> playerStart()
        }
    }

    private fun playerStart() {
        player.start()
        handler.post(setTrackCurTimeRunnable)
        binding.ibtnPlay.setImageResource(R.drawable.ic_pause)
        playerState = STATE_PLAYING
    }

    private fun playerPause() {
        player.pause()
        handler.removeCallbacks(setTrackCurTimeRunnable)
        binding.ibtnPlay.setImageResource(R.drawable.ic_play)
        playerState = STATE_PAUSED
    }

    private companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val TRACK_TIME_PATTERN = "mm:ss"
        const val SET_CURRENT_TRACK_TIME_DELAY_MILLIS = 500L
    }
}