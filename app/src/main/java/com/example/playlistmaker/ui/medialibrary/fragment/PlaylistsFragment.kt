package com.example.playlistmaker.ui.medialibrary.fragment

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.domain.medialibrary.models.Playlist
import com.example.playlistmaker.ui.medialibrary.adapter.PlaylistGridAdapter
import com.example.playlistmaker.ui.medialibrary.view_model.PlaylistsScreenState
import com.example.playlistmaker.ui.medialibrary.view_model.PlaylistsFragmentViewModel
import com.example.playlistmaker.util.BindingFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistsFragment:  BindingFragment<FragmentPlaylistsBinding>() {

    private val viewModel: PlaylistsFragmentViewModel by viewModel()

    private lateinit var playlistAdapter: PlaylistGridAdapter

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_addPlaylistFragment)
        }

        playlistAdapter = PlaylistGridAdapter { element, playlist ->
            onPlaylistLongClick(element, playlist)
        }
        binding.rvPlaylists.layoutManager =
            GridLayoutManager(requireContext(), COUNT_COLUMNS, GridLayoutManager.VERTICAL, false)
        binding.rvPlaylists.adapter = playlistAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.screenStateFlow.collect { state ->
                renderState(state)
            }
        }
    }

    private fun renderState(state: PlaylistsScreenState) {
        when (state) {
            is PlaylistsScreenState.Loading -> setLoadingState()
            is PlaylistsScreenState.NotFound -> setNotFoundState()
            is PlaylistsScreenState.Found -> setFoundState(state.playlists)
        }
    }

    private fun setLoadingState() {
        binding.rvPlaylists.isVisible = false
        binding.groupEmpty.isVisible = false

        binding.progressBar.isVisible = true
    }

    private fun setNotFoundState() {
        binding.ivNotFoundImage.isVisible = true;
        binding.tvNotFoundText.isVisible = true;
        binding.groupEmpty.isVisible = true

        binding.progressBar.isVisible = false
        binding.rvPlaylists.isVisible = false
    }

    private fun setFoundState(playlists: List<Playlist>) {
        playlistAdapter.updatePlaylists(playlists)
        binding.progressBar.isVisible = false
        binding.groupEmpty.isVisible = false
        binding.tvNotFoundText.isVisible = false
        binding.ivNotFoundImage.isVisible = false
        binding.rvPlaylists.isVisible = true
    }

    private fun onPlaylistLongClick(view: View, playlist: Playlist): Boolean {
        val popup = PopupMenu(requireContext(), view)

        popup.inflate(R.menu.playlist_menu)

        popup.setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.menu_item_delete -> {
                    File(
                        requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        playlist.title
                    ).delete()
                    viewModel.deletePlaylist(playlist)
                }
            }
            false
        }
        popup.show()
        return true
    }

    override fun onStart() {
        super.onStart()
        viewModel.updatePlaylists()
    }

    override fun onDestroyView() {
        binding.rvPlaylists.adapter = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): PlaylistsFragment = PlaylistsFragment()
        private const val COUNT_COLUMNS = 2
    }
}