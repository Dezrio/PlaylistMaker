package com.example.playlistmaker.ui.favorites.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavouriteTracksBinding
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.ui.favorites.view_model.FavouriteTracksFragmentViewModel
import com.example.playlistmaker.ui.favorites.view_model.FavouriteTracksScreenState
import com.example.playlistmaker.ui.medialibrary.fragment.MediaLibraryFragmentDirections
import com.example.playlistmaker.ui.search.adapter.TrackAdapter
import com.example.playlistmaker.util.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : BindingFragment<FragmentFavouriteTracksBinding>() {

    private val viewModel: FavouriteTracksFragmentViewModel by viewModel()

    private lateinit var trackAdapter: TrackAdapter

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavouriteTracksBinding {
        return FragmentFavouriteTracksBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackAdapter = TrackAdapter { onTrackClick(it) }
        binding.rvTrackList.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )

        binding.rvTrackList.adapter = trackAdapter

        viewModel.observeScreenState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavouriteTracksScreenState.Loading -> setLoadingState()
                is FavouriteTracksScreenState.NotFound -> setNotFoundState()
                is FavouriteTracksScreenState.Found -> setFoundState(state.tracks)
            }
        }

        viewModel.observeOnTrackClickLiveData().observe(viewLifecycleOwner) { track ->
            val action = MediaLibraryFragmentDirections.actionMediaLibraryFragmentToAudioPlayerActivity(
                track
            )
            parentFragment?.findNavController()?.navigate(action)
        }
    }

    private fun onTrackClick(track: Track) {
        viewModel.onTrackClick(track)
    }

    private fun setLoadingState() {
        binding.rvTrackList.isVisible = false
        binding.groupEmpty.isVisible = false
        binding.ivNotFoundImage.isVisible = false
        binding.tvNotFoundText.isVisible = false

        binding.progressBarFavorites.isVisible = true
    }

    private fun setFoundState(tracks: List<Track>) {
        binding.groupEmpty.isVisible = false
        binding.ivNotFoundImage.isVisible = false
        binding.tvNotFoundText.isVisible = false
        binding.progressBarFavorites.isVisible = false

        trackAdapter.updateTracks(tracks)
        binding.rvTrackList.isVisible = true
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadTracks()
    }

    override fun onDestroyView() {
        binding.rvTrackList.adapter = null
        super.onDestroyView()
    }

    private fun setNotFoundState() {
        binding.tvNotFoundText.text = requireActivity().getString(R.string.media_library_empty_favourite_tracks_text)
        binding.groupEmpty.isVisible = true
        binding.ivNotFoundImage.isVisible = true
        binding.tvNotFoundText.isVisible = true
        binding.rvTrackList.isVisible = false
        binding.progressBarFavorites.isVisible = false
    }

    companion object{
        fun newInstance() : FavoriteTracksFragment = FavoriteTracksFragment()
    }
}