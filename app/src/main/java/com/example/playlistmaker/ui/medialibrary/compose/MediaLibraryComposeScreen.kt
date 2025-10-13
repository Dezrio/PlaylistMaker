package com.example.playlistmaker.ui.medialibrary.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.compose.components.CommonToolbar
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.ui.favorites.compose.FavouriteTracksComposeScreen
import kotlinx.coroutines.launch

@Composable
fun MediaLibraryComposeScreen(
    openAudioPlayer: (Track) -> Unit,
    openAddPlaylist: () -> Unit,
    openPlaylist: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { LibraryTabs.entries.size })
    val selectedTabIndex by remember { derivedStateOf { pagerState.currentPage } }

    Scaffold(
        topBar = { CommonToolbar(stringResource(R.string.btn_library)) },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                SecondaryTabRow (
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    indicator = {
                        SecondaryIndicator(
                            modifier = Modifier
                                .tabIndicatorOffset(selectedTabIndex)
                                .padding(horizontal = 16.dp)
                                .height(2.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    divider = { HorizontalDivider(color = MaterialTheme.colorScheme.primary) }
                ) {
                    LibraryTabs.entries.forEach { tab ->
                        val index = tab.ordinal
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = {
                                scope.launch { pagerState.animateScrollToPage(index) }
                            },
                            text = {
                                Text(
                                    text = when (tab) {
                                        LibraryTabs.Favorites -> stringResource(R.string.media_library_tab_favourite_tracks)
                                        LibraryTabs.Playlists -> stringResource(R.string.media_library_tab_playlists)
                                    },
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            },
                            selectedContentColor = MaterialTheme.colorScheme.secondary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalAlignment = Alignment.Top
                ) { page ->
                    when (LibraryTabs.entries[page]) {
                        LibraryTabs.Favorites -> FavouriteTracksComposeScreen(
                            openAudioPlayer = openAudioPlayer
                        )
                        LibraryTabs.Playlists -> PlaylistsComposeScreen(
                            openAddPlaylist = openAddPlaylist,
                            openPlaylist = openPlaylist
                        )
                    }
                }
            }
        }
    )
}

enum class LibraryTabs {
    Favorites,
    Playlists
}