package com.example.playlistmaker.ui.search.fragment

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
import com.example.playlistmaker.ui.search.compose.SearchComposeScreen

class SearchFragment : Fragment() {
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
                    activity?.let {
                        SearchComposeScreen(
                            openAudioPlayer = ::openAudioPlayer
                        )
                    }
                }
            }
        }
        return composeView
    }

    private fun openAudioPlayer(track: Track) {
        val action = SearchFragmentDirections.actionSearchFragmentToAudioPlayerActivity(track)
        parentFragment?.findNavController()?.navigate(action)
    }
}