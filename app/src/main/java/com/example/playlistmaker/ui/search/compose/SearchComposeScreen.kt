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
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.playlistmaker.R
import com.example.playlistmaker.compose.components.CommonButton
import com.example.playlistmaker.compose.components.CommonErrorMessage
import com.example.playlistmaker.compose.components.CommonProgressBar
import com.example.playlistmaker.compose.components.CommonTextField
import com.example.playlistmaker.compose.components.CommonToolbar
import com.example.playlistmaker.compose.components.CommonTrackList
import com.example.playlistmaker.compose.components.CommonTrackListWithButton
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
    val searchTextState by viewModel.searchTextStateFlow.collectAsStateWithLifecycle()
    val eventState by viewModel.eventState.collectAsStateWithLifecycle(initialValue = null)

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LifecycleStartEffect(Unit) {
        viewModel.prepareSearch()
        onStopOrDispose { }
    }

    LaunchedEffect(eventState) {
        eventState?.let {
            focusManager.clearFocus()
            openAudioPlayer(it)
        }
    }

    Scaffold(
        topBar = { CommonToolbar(stringResource(R.string.btn_search)) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchBarSection(
                text = searchTextState,
                onTextChanged = viewModel::onSearchTextChange,
                onFocusChanged = viewModel::onSearchTextFocusChange,
                onClear = {
                    focusManager.clearFocus()
                    viewModel.clearSearchText()
                },
                focusRequester = focusRequester
            )

            SearchContentSection(
                screenState = screenState,
                onTrackClick = viewModel::onTrackClick,
                onRefresh = viewModel::refreshSearch,
                onClearHistory = viewModel::clearHistory
            )
        }
    }
}

@Composable
private fun SearchBarSection(
    text: String,
    onTextChanged: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onClear: () -> Unit,
    focusRequester: FocusRequester
) {
    CommonTextField(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .focusRequester(focusRequester),
        text = text,
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.search_icon),
                contentDescription = stringResource(R.string.btn_search),
                tint = MaterialTheme.colorScheme.onSecondary
            )
        },
        trailingIcon = {
            Icon(
                painter = painterResource(R.drawable.clear),
                contentDescription = stringResource(R.string.search_clear_text),
                tint = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.clickable { onClear() }
            )
        },
        placeholderText = stringResource(R.string.btn_search),
        onTextChanged = onTextChanged,
        onFocusChanged = onFocusChanged
    )
}

@Composable
private fun SearchContentSection(
    screenState: SearchScreenState,
    onTrackClick: (Track) -> Unit,
    onRefresh: () -> Unit,
    onClearHistory: () -> Unit
) {
    when (screenState) {
        is SearchScreenState.DefaultScreenState,
        is SearchScreenState.TextEnterScreenState -> {}

        is SearchScreenState.LoadingScreenState -> CommonProgressBar()

        is SearchScreenState.TrackScreenState -> {
            CommonTrackList(
                tracks = screenState.tracks.toImmutableList(),
                onTrackClick = onTrackClick
            )
        }

        is SearchScreenState.NotFoundScreenState -> {
            SearchComposeScreenError(true, onRefresh)
        }

        is SearchScreenState.ErrorScreenState -> {
            SearchComposeScreenError(false, onRefresh)
        }

        is SearchScreenState.TrackHistoryScreenState -> {
            SearchHistoryBlock(
                tracks = screenState.tracks.toImmutableList(),
                onTrackClick = onTrackClick,
                onClearHistoryClick = onClearHistory
            )
        }
    }
}

@Composable
fun SearchComposeScreenError(isEmpty: Boolean,onUpdateClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (isEmpty){
            CommonErrorMessage(
                message = stringResource(R.string.not_found_search_text),
                iconId = R.drawable.not_found_compose,
                topPadding
            )
        } else {
            CommonErrorMessage(
                message = stringResource(R.string.error_search_text),
                iconId = R.drawable.error_compose,
                topPadding
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        CommonButton(stringResource(R.string.refresh_search_text), onClick = onUpdateClick)
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
        CommonTrackListWithButton(
            tracks = tracks,
            onTrackClick = onTrackClick,
            buttonTitle = stringResource(R.string.history_search_clear_text),
            onButtonClick = onClearHistoryClick
        )
    }
}

const val topPadding = 110