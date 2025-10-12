package com.example.playlistmaker.ui.medialibrary.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.compose.components.ComposeButton
import com.example.playlistmaker.compose.components.ComposeErrorMessage
import com.example.playlistmaker.compose.components.ComposeProgressBar
import com.example.playlistmaker.domain.medialibrary.models.Playlist
import com.example.playlistmaker.ui.medialibrary.view_model.PlaylistsFragmentViewModel
import com.example.playlistmaker.ui.medialibrary.view_model.PlaylistsScreenState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlaylistsComposeScreen(
    viewModel: PlaylistsFragmentViewModel = koinViewModel(),
    openAddPlaylist: () -> Unit,
    openPlaylist: (Int) -> Unit
) {
    val screenState by viewModel.screenStateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.updatePlaylists()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (screenState) {
                is PlaylistsScreenState.Loading -> {
                    ComposeProgressBar()
                }

                is PlaylistsScreenState.Found -> {
                    ComposePlaylistsContent(onClick = openAddPlaylist)
                    ComposePlaylistsGrid(
                        playlists = (screenState as PlaylistsScreenState.Found)
                            .playlists
                            .toImmutableList(),
                        onPlaylistClick = openPlaylist
                    )
                }

                is PlaylistsScreenState.NotFound -> {
                    ComposePlaylistsContent(onClick = openAddPlaylist)
                    ComposeErrorMessage(
                        stringResource(R.string.empty_playlist_track_list_message),
                        R.drawable.not_found_compose,
                        46
                    )
                }
            }
        }
    }
}

@Composable
fun ComposePlaylistsGrid(
    playlists: ImmutableList<Playlist>,
    onPlaylistClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .padding(top = 12.dp, bottom = 60.dp)
            .padding(horizontal = 12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = playlists,
            key = { it.id }
        ) { playlist ->
            ComposePlaylistsGridItem(playlist, onPlaylistClick)
        }
    }
}

@Composable
fun ComposePlaylistsGridItem(
    playlist: Playlist,
    onPlaylistClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clickable { onPlaylistClick(playlist.id) }
    ) {
        ComposePlaylistCover(
            url = playlist.coverPath,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth()
                .aspectRatio(1f)
        )

        Text(
            text = playlist.title,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            maxLines = 2,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
        )

        Text(
            text = pluralStringResource(
                R.plurals.track_plurals,
                playlist.tracksCount,
                playlist.tracksCount
            ),
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 4.dp)
        )
    }
}

@Composable
fun ComposePlaylistCover(
    url: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = url,
        contentDescription = stringResource(R.string.playlist_cover_description),
        placeholder = painterResource(R.drawable.player_placeholder),
        error = painterResource(R.drawable.player_placeholder),
        contentScale = ContentScale.Crop,
        modifier = modifier.clip(RoundedCornerShape(8.dp))
    )
}

@Composable
fun ComposePlaylistsContent(onClick: () -> Unit) {
    Spacer(modifier = Modifier.height(24.dp))
    ComposeButton(stringResource(R.string.media_library_btn_new_playlist)) {
        onClick()
    }
}