package com.example.playlistmaker.compose.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ComposeTrackList(tracks: ImmutableList<Track>, onTrackClick: (Track) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.padding(top = 16.dp)
    ) {
        items(tracks) { track ->
            ComposeTrackItem(track, onTrackClick)
        }
    }
}

@Composable
fun ComposeTrackItem(track: Track, onTrackClick: (Track) -> Unit) {
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
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = track.artistName,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(R.drawable.dot),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = trackDuration,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
        placeholder = painterResource(R.drawable.track_placeholder),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(45.dp)
            .clip(RoundedCornerShape(2.dp)),
        error = painterResource(R.drawable.track_placeholder)
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
            ComposeTrackItem(track, onTrackClick)
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
            ComposeButton(title = buttonTitle, onClick = onButtonClick)
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}