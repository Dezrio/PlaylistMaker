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
import com.example.playlistmaker.compose.components.ComposeErrorMessage
import com.example.playlistmaker.compose.components.ComposeProgressBar
import com.example.playlistmaker.compose.components.ComposeTrackList
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
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (screenState) {
                is FavouriteTracksScreenState.Loading -> {
                    ComposeProgressBar()
                }

                is FavouriteTracksScreenState.Found -> {
                    ComposeTrackList(
                        tracks = (screenState as FavouriteTracksScreenState.Found).tracks.toImmutableList(),
                        onTrackClick = viewModel::onTrackClick
                    )
                }

                is FavouriteTracksScreenState.NotFound -> {
                    ComposeErrorMessage(
                        stringResource(R.string.not_found_search_text),
                        R.drawable.not_found_compose,
                        106
                    )
                }
            }
        }
    }
}