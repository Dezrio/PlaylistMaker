package com.example.playlistmaker.ui.favorites.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.playlistmaker.R
import com.example.playlistmaker.compose.components.CommonErrorMessage
import com.example.playlistmaker.compose.components.CommonProgressBar
import com.example.playlistmaker.compose.components.CommonTrackList
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.ui.favorites.view_model.FavouriteTracksFragmentViewModel
import com.example.playlistmaker.ui.favorites.view_model.FavouriteTracksScreenState
import kotlinx.collections.immutable.toImmutableList
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavouriteTracksComposeScreen(
    viewModel: FavouriteTracksFragmentViewModel = koinViewModel(),
    openAudioPlayer: (Track) -> Unit
) {
    val screenState by viewModel.screenStateFlow.collectAsStateWithLifecycle()
    val track by viewModel.onTrackClickStateFlow.collectAsStateWithLifecycle(initialValue = null)

    LifecycleStartEffect(Unit) {
        viewModel.prepareSearch()
        viewModel.loadTracks()
        onStopOrDispose { }
    }

    LaunchedEffect(track) {
        track?.let { openAudioPlayer(it) }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.primary
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            when (screenState) {
                is FavouriteTracksScreenState.Loading -> {
                    CommonProgressBar()
                }

                is FavouriteTracksScreenState.Found -> {
                    CommonTrackList(
                        tracks = (screenState as FavouriteTracksScreenState.Found).tracks.toImmutableList(),
                        onTrackClick = viewModel::onTrackClick
                    )
                }

                is FavouriteTracksScreenState.NotFound -> {
                    CommonErrorMessage(
                        stringResource(R.string.media_library_empty_favourite_tracks_text),
                        R.drawable.not_found_compose,
                        106
                    )
                }
            }
        }
    }
}