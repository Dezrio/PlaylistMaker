package com.example.playlistmaker.ui.medialibrary.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.ui.medialibrary.view_model.PlaylistsFragmentViewModel
import com.example.playlistmaker.ui.medialibrary.view_model.PlaylistsScreenState
import com.example.playlistmaker.util.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment: BindingFragment<FragmentPlaylistsBinding>() {

    private val viewModel: PlaylistsFragmentViewModel by viewModel()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.screenStateObserve().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistsScreenState.Default -> setDefaultState()
                is PlaylistsScreenState.NotFound -> setNotFoundState()
            }
        }
    }

    private fun setNotFoundState() {
        binding.tvNotFoundText.text = requireActivity().getString(R.string.media_library_empty_playlists_text)

        binding.groupEmpty.isVisible = true
    }

    private fun setDefaultState(){
        binding.groupEmpty.isVisible = false
    }

    companion object{
        fun newInstance() : PlaylistsFragment = PlaylistsFragment()
    }
}