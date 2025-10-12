package com.example.playlistmaker.ui.favorites.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.compose.components.ComposeErrorMessage
import com.example.playlistmaker.compose.components.ComposeProgressBar
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.ui.favorites.view_model.FavouriteTracksFragmentViewModel
import com.example.playlistmaker.ui.favorites.view_model.FavouriteTracksScreenState
import kotlinx.collections.immutable.ImmutableList
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
                    TrackList(
                        tracks = (screenState as FavouriteTracksScreenState.Found).tracks.toImmutableList(),
                        onTrackClick = viewModel::onTrackClick
                    )
                }

                is FavouriteTracksScreenState.NotFound -> {
                    ComposeErrorMessage(
                        stringResource(R.string.not_found_search_text),
                        R.drawable.not_found_light,
                        106
                    )
                }
            }
        }
    }
}

@Composable
fun TrackList(
    tracks: ImmutableList<Track>,
    onTrackClick: (Track) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.padding(top = 16.dp)
    ) {
        items(tracks) { track ->
            TrackItem(track, onTrackClick)
        }
    }
}

@Composable
fun TrackItem(
    track: Track,
    onTrackClick: (Track) -> Unit
) {
    val trackDuration = track.trackTime.ifEmpty { stringResource(R.string.track_time_placeholder) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 12.dp)
            .clickable { onTrackClick(track) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        TrackCoverMini(track.artworkUrl100)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = track.trackName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = track.artistName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Icon(
                    painter = painterResource(R.drawable.dot),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Text(
                    text = trackDuration,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Icon(
            painter = painterResource(R.drawable.arrow_forward),
            contentDescription = stringResource(R.string.track_details),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TrackCoverMini(url: String) {
    AsyncImage(
        model = url,
        contentDescription = stringResource(R.string.track_cover_player_text),
        placeholder = painterResource(R.drawable.player_placeholder),
        error = painterResource(R.drawable.player_placeholder),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(45.dp)
            .clip(RoundedCornerShape(4.dp))
    )
}

@Composable
fun TrackListWithButton(
    tracks: ImmutableList<Track>,
    onTrackClick: (Track) -> Unit,
    buttonTitle: String,
    onButtonClick: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 16.dp)
    ) {
        items(tracks) { track ->
            TrackItem(track, onTrackClick)
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onButtonClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = buttonTitle,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}