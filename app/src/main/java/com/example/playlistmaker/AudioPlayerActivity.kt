package com.example.playlistmaker

import android.os.Bundle
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
import dataclasses.Track

class AudioPlayerActivity : AppCompatActivity() {
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
    }
}