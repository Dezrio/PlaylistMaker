package com.example.playlistmaker.ui.search.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.data.search.enums.SearchResultStates
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.search.api.interactor.TracksSearchInteractor
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.ui.App.Companion.TRACK_KEY
import com.example.playlistmaker.ui.player.activity.AudioPlayerActivity
import com.example.playlistmaker.ui.search.adapter.TrackAdapter
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.google.gson.Gson

class SearchActivity : AppCompatActivity() {
    private val viewModel: SearchViewModel by viewModels<SearchViewModel> {
        SearchViewModel.getViewModelFactory()
    }

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
            binding.llTrackHistory.visibility = View.GONE
            hideKeyboard()
        }
    }

    private fun initAdapter(){
        trackAdapter = TrackAdapter { onTrackClick(it) }
        binding.rvTrack.adapter = trackAdapter
        binding.rvTrack.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        viewModel.getTrackLiveData().observe(this) {

        }
    }

    private fun initHistoryAdapter(){
        trackHistoryAdapter = TrackAdapter { onTrackClick(it) }
        binding.rvTrackHistory.adapter = trackHistoryAdapter
        binding.rvTrackHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    }

    private fun setDefaultScreenState(){
        setScreenState(
            false,
            false,
            false,
            false,
            false)
    }

    private fun setLoadingScreenState(){
        setScreenState(
            true,
            false,
            false,
            false,
            false)
    }

    private fun setTrackScreenState(){
        setScreenState(
            false,
            true,
            false,
            false,
            false)
    }

    private fun setTrackHistoryScreenState(){
        setScreenState(
            false,
            false,
            true,
            false,
            false)
    }

    private fun setNotFoundScreenState(){
        setScreenState(
            false,
            false,
            false,
            true,
            false)
    }

    private fun setErrorScreenState(){
        setScreenState(
            false,
            false,
            false,
            false,
            true)
    }

    private fun setScreenState(isProgressBarVisible: Boolean,
                               isTrackListVisible: Boolean,
                               isTrackHistoryListVisible: Boolean,
                               isNotFoundVisible: Boolean,
                               isErrorVisible: Boolean){
        binding.progressBar.isVisible = isProgressBarVisible
        binding.rvTrack.isVisible = isTrackListVisible
        binding.rvTrackHistory.isVisible = isTrackHistoryListVisible
        binding.llNotFoundSearch.isVisible = isNotFoundVisible
        binding.llErrorSearch.isVisible = isErrorVisible
    }

    private fun onTrackClick(track: Track){
        viewModel.onTrackClick(track)
    }

    private fun openAudioPlayer(track: Track, isAfterSave: Boolean = false){
        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra(TRACK_KEY, Gson().toJson(track))
        startActivity(intent)
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }
}