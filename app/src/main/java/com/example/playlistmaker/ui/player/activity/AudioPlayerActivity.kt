package com.example.playlistmaker.ui.player.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.domain.medialibrary.models.Playlist
import com.example.playlistmaker.domain.player.models.AudioPlayerState
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.ui.medialibrary.adapter.PlaylistVerticalAdapter
import com.example.playlistmaker.ui.medialibrary.fragment.AddPlaylistFragment
import com.example.playlistmaker.ui.medialibrary.view_model.AddPlaylistResultScreenState
import com.example.playlistmaker.ui.medialibrary.view_model.PlaylistsScreenState
import com.example.playlistmaker.ui.player.view_model.AudioPlayerViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var track: Track
    private lateinit var playlistAdapter: PlaylistVerticalAdapter
    private val args: AudioPlayerActivityArgs by navArgs()
    private val viewModel: AudioPlayerViewModel by lazy {
        getViewModel { parametersOf(track) }
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

        track = args.track

        viewModel.getAudioPlayerLiveData().observe(this) { data ->
            when (data.audioPlayerState) {
                AudioPlayerState.STATE_DEFAULT -> {
                    showDefaultState(data.track, data.trackCurTime)
                }

                AudioPlayerState.STATE_PREPARED -> {
                    showPreparedState(data.track, data.trackCurTime)
                }

                AudioPlayerState.STATE_PLAYING -> {
                    showPlayingState(data.track, data.trackCurTime)
                }

                AudioPlayerState.STATE_PAUSED -> {
                    showPauseState(data.track, data.trackCurTime)
                }
            }
        }

        binding.ibtnPlay.setOnClickListener {
            viewModel.playerControl()
        }

        binding.ibtnLike.setOnClickListener {
            viewModel.onFavoriteClick()
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        playlistAdapter = PlaylistVerticalAdapter { playlist ->
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            viewModel.addTrackToPlaylist(playlist)
        }

        binding.rvPlaylists.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvPlaylists.adapter = playlistAdapter

        binding.ibtnAddTrackToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            viewModel.btnAddTrackToPlaylistClicked()
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.vOverlay.isVisible = false
                    }

                    else -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.vOverlay.isVisible = true
                binding.vOverlay.alpha = (slideOffset + 1) / 2
            }
        })

        viewModel.getPlaylistsLiveData().observe(this) { state ->
            when (state) {
                is PlaylistsScreenState.Loading -> {
                    setLoadingPlaylistsState()
                }

                is PlaylistsScreenState.NotFound -> {
                    setNotFoundPlaylistsState()
                }

                is PlaylistsScreenState.Found -> {
                    setFoundPlaylistsState(state.playlists)
                }
            }
        }

        viewModel.getAddTrackLiveData().observe(this) { state ->
            when (state) {
                is AddPlaylistResultScreenState.Created -> {
                    Snackbar.make(
                        binding.rvPlaylists,
                        "${getString(R.string.toast_success_adding_track_to_playlist)} ${state.playlistTitle}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                is AddPlaylistResultScreenState.AlreadyExists -> Snackbar.make(
                    binding.rvPlaylists,
                    "${getString(R.string.toast_track_already_exists_in_playlist)} ${state.playlistTitle}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        binding.btnCreatePlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            supportFragmentManager.commit {
                add(R.id.player_fragment_container, AddPlaylistFragment.newInstance(-1))
                setReorderingAllowed(true)
                addToBackStack(null)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.playerPause()
    }

    private fun setLoadingPlaylistsState() {
        binding.rvPlaylists.isVisible = false

        binding.progressBarPlaylists.isVisible = true
    }

    private fun setNotFoundPlaylistsState() {
        binding.rvPlaylists.isVisible = false
        binding.progressBarPlaylists.isVisible = false
    }

    private fun setFoundPlaylistsState(playlists: List<Playlist>) {
        playlistAdapter.updatePlaylists(playlists)

        binding.progressBarPlaylists.isVisible = false

        binding.rvPlaylists.isVisible = true
    }

    private fun showDefaultState(track: Track, trackCurTime: String){
        binding.ibtnLike.isSelected = track.isFavorite
        binding.tvAlbum.isVisible = false
        binding.tvAlbumText.isVisible = false
        binding.tvTrackCurrentTime.text  = trackCurTime
        trackInit(track)
    }

    private fun showPreparedState(track: Track, trackCurTime: String){
        binding.ibtnLike.isSelected = track.isFavorite
        binding.ibtnPlay.isEnabled = true
        binding.tvTrackCurrentTime.text  = trackCurTime
        binding.ibtnPlay.setImageResource(R.drawable.ic_play)
    }

    private fun showPlayingState(track: Track, trackCurTime: String){
        binding.ibtnLike.isSelected = track.isFavorite
        binding.tvTrackCurrentTime.text  = trackCurTime
        binding.ibtnPlay.setImageResource(R.drawable.ic_pause)
    }

    private fun showPauseState(track: Track, trackCurTime: String){
        binding.ibtnLike.isSelected = track.isFavorite
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