package com.example.playlistmaker.ui.medialibrary.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.compose.AppTheme
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.ui.medialibrary.compose.MediaLibraryComposeScreen

class MediaLibraryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val composeView = ComposeView(requireContext())

        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    MediaLibraryComposeScreen(
                        openAudioPlayer = ::openAudioPlayer,
                        openAddPlaylist = ::openAddPlaylist,
                        openPlaylist = ::openPlaylist,
                    )
                }
            }
        }
        return composeView
    }

    private fun openAudioPlayer(track: Track) {
        val action = MediaLibraryFragmentDirections.actionMediaLibraryFragmentToAudioPlayerActivity(track)
        parentFragment?.findNavController()?.navigate(action)
    }

    private fun openAddPlaylist() {
        val action = MediaLibraryFragmentDirections.actionMediaLibraryFragmentToAddPlaylistFragment()
        parentFragment?.findNavController()?.navigate(action)
    }

    private fun openPlaylist(playlistId: Int) {
        val action = MediaLibraryFragmentDirections.actionMediaLibraryFragmentToPlaylistFragment(playlistId)
        parentFragment?.findNavController()?.navigate(action)
    }
}