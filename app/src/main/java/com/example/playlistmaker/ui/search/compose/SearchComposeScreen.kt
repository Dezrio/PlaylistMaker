package com.example.playlistmaker.ui.search.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.playlistmaker.R
import com.example.playlistmaker.compose.components.ComposeButton
import com.example.playlistmaker.compose.components.ComposeErrorMessage
import com.example.playlistmaker.compose.components.ComposeProgressBar
import com.example.playlistmaker.compose.components.ComposeTextField
import com.example.playlistmaker.compose.components.ComposeToolbar
import com.example.playlistmaker.compose.components.ComposeTrackList
import com.example.playlistmaker.compose.components.ComposeTrackListWithButton
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.ui.search.view_model.SearchScreenState
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchComposeScreen(
    viewModel: SearchViewModel = koinViewModel(),
    openAudioPlayer: (Track) -> Unit
) {
    val screenState by viewModel.screenStateFlow.collectAsStateWithLifecycle()
    val eventState by viewModel.eventState.collectAsStateWithLifecycle(initialValue = null)

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(eventState) {
        eventState?.let {
            focusManager.clearFocus()
            openAudioPlayer(it)
        }
    }

    Scaffold(
        topBar = { ComposeToolbar(stringResource(R.string.btn_search)) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.primary),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ComposeTextField(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .focusRequester(focusRequester),
                text = screenState.searchText,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.search_icon),
                        contentDescription = stringResource(R.string.btn_search),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.clear),
                        contentDescription = stringResource(R.string.search_clear_text),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.clickable {
                            focusManager.clearFocus()
                            viewModel.clearSearchText()
                        }
                    )
                },
                placeholderText = stringResource(R.string.btn_search),
                onTextChanged = viewModel::onSearchTextChange,
                onFocusChanged = viewModel::onSearchTextFocusChange
            )

            when (screenState) {
                is SearchScreenState.DefaultScreenState,
                is SearchScreenState.TextEnterScreenState -> {}

                is SearchScreenState.LoadingScreenState -> ComposeProgressBar()

                is SearchScreenState.TrackScreenState -> {
                    ComposeTrackList(
                        tracks = (screenState as SearchScreenState.TrackScreenState).tracks.toImmutableList(),
                        onTrackClick = viewModel::onTrackClick
                    )
                }

                is SearchScreenState.NotFoundScreenState -> {
                    SearchComposeScreenError(
                        true,
                        viewModel::refreshSearch
                    )
                }

                is SearchScreenState.ErrorScreenState -> {
                    SearchComposeScreenError(
                        false,
                        viewModel::refreshSearch
                    )
                }

                is SearchScreenState.TrackHistoryScreenState -> {
                    SearchHistoryBlock(
                        tracks = (screenState as SearchScreenState.TrackHistoryScreenState).tracks.toImmutableList(),
                        onTrackClick = viewModel::onTrackClick,
                        onClearHistoryClick = viewModel::clearHistory
                    )
                }
            }
        }
    }
}

@Composable
fun SearchComposeScreenError(isEmpty: Boolean,onUpdateClick: () -> Unit) {
    val topPadding = 110

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (isEmpty){
            ComposeErrorMessage(
                message = stringResource(R.string.not_found_search_text),
                iconId = R.drawable.not_found_dark,
                topPadding
            )
        } else {
            ComposeErrorMessage(
                message = stringResource(R.string.error_search_text),
                iconId = R.drawable.not_found_dark,
                topPadding
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        ComposeButton(stringResource(R.string.refresh_search_text), onClick = onUpdateClick)
    }
}

@Composable
fun SearchHistoryBlock(
    tracks: ImmutableList<Track>,
    onTrackClick: (Track) -> Unit,
    onClearHistoryClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(42.dp))
        Text(
            text = stringResource(R.string.history_search_text),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        ComposeTrackListWithButton(
            tracks = tracks,
            onTrackClick = onTrackClick,
            buttonTitle = stringResource(R.string.history_search_clear_text),
            onButtonClick = onClearHistoryClick
        )
    }
}