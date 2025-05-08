package com.example.playlistmaker.ui.search.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.ui.App.Companion.TRACK_KEY
import com.example.playlistmaker.ui.player.activity.AudioPlayerActivity
import com.example.playlistmaker.ui.search.adapter.TrackAdapter
import com.example.playlistmaker.ui.search.view_model.ScreenState
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {
    private val viewModel: SearchViewModel by viewModel()

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var trackHistoryAdapter: TrackAdapter
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.settingsArrowBack.setOnClickListener {
            finish()
        }

        initAdapter()
        initHistoryAdapter()

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onSearchTextFocusChange(hasFocus)
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onSearchTextChange(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.searchEditText.addTextChangedListener(textWatcher)

        binding.btnRefreshSearch.setOnClickListener {
            viewModel.refreshSearch()
        }

        binding.clearText.setOnClickListener {
            hideKeyboard()
            viewModel.clearSearchText()
            trackAdapter
            binding.searchEditText.clearFocus()
        }

        binding.btnClearTrackHistory.setOnClickListener{
            viewModel.clearHistory()
            trackHistoryAdapter.notifyDataSetChanged()
            binding.llTrackHistory.isVisible = false
            hideKeyboard()
        }

        viewModel.getEventLiveData().observe(this) { trackId ->
            trackId?.let { openAudioPlayer(trackId) }
        }

        viewModel.getScreenSateLiveData().observe(this) { screenState ->
            when (screenState){
                is ScreenState.DefaultScreenState -> {
                    setDefaultScreenState()
                }
                is ScreenState.LoadingScreenState -> {
                    setLoadingScreenState()
                }
                is ScreenState.TrackScreenState -> {
                    setTrackScreenState()
                }
                is ScreenState.TrackHistoryScreenState -> {
                    setTrackHistoryScreenState()
                }
                is ScreenState.NotFoundScreenState -> {
                    setNotFoundScreenState()
                }
                is ScreenState.ErrorScreenState -> {
                    setErrorScreenState()
                }
                is ScreenState.TextEnterScreenState -> {
                    setTextEnterScreenState()
                }
            }
        }
    }

    private fun initAdapter(){
        trackAdapter = TrackAdapter { onTrackClick(it) }
        binding.rvTrack.adapter = trackAdapter
        binding.rvTrack.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        viewModel.getTrackLiveData().observe(this) { tracks ->
            trackAdapter.updateTracks(tracks)
        }
    }

    private fun initHistoryAdapter(){
        trackHistoryAdapter = TrackAdapter { onTrackClick(it) }
        binding.rvTrackHistory.adapter = trackHistoryAdapter
        binding.rvTrackHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        viewModel.getTracksHistoryLiveData().observe(this) { tracks ->
            trackHistoryAdapter.updateTracks(tracks)
        }
    }

    private fun setDefaultScreenState(){
        hideKeyboard()
        setScreenState(
            isProgressBarVisible = false,
            isTrackListVisible = false,
            isTrackHistoryListVisible = false,
            isNotFoundVisible = false,
            isErrorVisible = false,
            isClearTextVisible = false
        )

        binding.searchEditText.setText(getString(R.string.empty_string))
    }

    private fun setLoadingScreenState(){
        hideKeyboard()
        setScreenState(
            isProgressBarVisible = true,
            isTrackListVisible = false,
            isTrackHistoryListVisible = false,
            isNotFoundVisible = false,
            isErrorVisible = false,
            isClearTextVisible = true
        )
    }

    private fun setTrackScreenState(){
        hideKeyboard()
        setScreenState(
            isProgressBarVisible = false,
            isTrackListVisible = true,
            isTrackHistoryListVisible = false,
            isNotFoundVisible = false,
            isErrorVisible = false,
            isClearTextVisible = true
        )
    }

    private fun setTrackHistoryScreenState(){
        setScreenState(
            isProgressBarVisible = false,
            isTrackListVisible = false,
            isTrackHistoryListVisible = true,
            isNotFoundVisible = false,
            isErrorVisible = false
        )
    }

    private fun setNotFoundScreenState(){
        setScreenState(
            isProgressBarVisible = false,
            isTrackListVisible = false,
            isTrackHistoryListVisible = false,
            isNotFoundVisible = true,
            isErrorVisible = false
        )
    }

    private fun setErrorScreenState(){
        setScreenState(
            isProgressBarVisible = false,
            isTrackListVisible = false,
            isTrackHistoryListVisible = false,
            isNotFoundVisible = false,
            isErrorVisible = true
        )
    }

    private fun setTextEnterScreenState(){
        setScreenState(
            isClearTextVisible = true
        )
    }

    private fun setScreenState(isProgressBarVisible: Boolean? = null,
                               isTrackListVisible: Boolean? = null,
                               isTrackHistoryListVisible: Boolean? = null,
                               isNotFoundVisible: Boolean? = null,
                               isErrorVisible: Boolean? = null,
                               isClearTextVisible: Boolean? = null){
        isProgressBarVisible?.let { binding.progressBar.isVisible = isProgressBarVisible }
        isTrackListVisible?.let { binding.rvTrack.isVisible = isTrackListVisible }
        isTrackHistoryListVisible?.let { binding.llTrackHistory.isVisible = isTrackHistoryListVisible }
        isNotFoundVisible?.let { binding.llNotFoundSearch.isVisible = isNotFoundVisible }
        isErrorVisible?.let { binding.llErrorSearch.isVisible = isErrorVisible }
        isClearTextVisible?.let { binding.clearText.isVisible = isClearTextVisible }
    }

    private fun onTrackClick(track: Track){
        viewModel.onTrackClick(track)
    }

    private fun openAudioPlayer(trackId: Int) {
        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra(TRACK_KEY, trackId)
        startActivity(intent)
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }
}