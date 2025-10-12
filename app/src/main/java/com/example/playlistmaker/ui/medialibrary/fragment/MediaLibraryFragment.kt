package com.example.playlistmaker.ui.medialibrary.fragment

import android.os.Bundle
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
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val composeView = ComposeView(requireContext())

        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    activity?.let {
                        MediaLibraryComposeScreen(
                            openAudioPlayer = ::openAudioPlayer,
                            navigateToModifyPlaylistScreen = ::navigateToModifyPlaylistScreen,
                            navigateToOnePlaylistScreen = ::navigateToOnePlaylistScreen,
                        )
                    }
                }
            }
        }
        return composeView
    }

    fun openAudioPlayer(track: Track) {
        val action = LibraryFragmentDirections.actionLibraryFragmentToAudioPlayerActivity(track)
        parentFragment?.findNavController()?.navigate(action)
    }

    fun navigateToModifyPlaylistScreen() {
        parentFragment?.findNavController()?.navigate(
            R.id.action_libraryFragment_to_modifyPlaylistFragment,
            ModifyPlaylistFragment.createArgs(UNKNOWN_ID)
        )
    }

    fun navigateToOnePlaylistScreen(playlistId: Int) {
        val action =
            LibraryFragmentDirections.actionLibraryFragmentToOnePlaylistFragment(playlistId)
        parentFragment?.findNavController()?.navigate(action)
    }
}