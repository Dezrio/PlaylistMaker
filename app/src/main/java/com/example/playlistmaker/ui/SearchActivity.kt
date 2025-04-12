package com.example.playlistmaker.ui

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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.App.Companion.TRACK_KEY
import com.example.playlistmaker.creator.HistoryCreator
import com.example.playlistmaker.creator.SearchCreator
import com.example.playlistmaker.data.enums.SearchResultStates
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.api.interactor.TracksSearchInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.TrackAdapter
import com.google.gson.Gson

class SearchActivity : AppCompatActivity() {
    private val tracksSearchInteractor = SearchCreator.provideTracksSearchInteractor()
    private val tracksHistoryInteractor = HistoryCreator.provideTracksHistoryInteractor()

    private var searchText: String = ""
    private var tracks: MutableList<Track> = mutableListOf()
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchTrack() }
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

        trackAdapter = TrackAdapter(tracks, ::saveTrackAndOpenAudioPlayer)
        binding.rvTrack.adapter = trackAdapter
        binding.rvTrack.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        trackHistoryAdapter = TrackAdapter(tracksHistoryInteractor.getHistory().toMutableList(), ::openAudioPlayer)
        binding.rvTrackHistory.adapter = trackHistoryAdapter
        binding.rvTrackHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearText.isVisible = !s.isNullOrEmpty()
                searchText = if (s.isNullOrEmpty()) getString(R.string.empty_string) else s.toString()

                changeTrackHistoryVisibility()

                if (s.isNullOrEmpty()){
                    tracks.clear()
                    trackAdapter.notifyDataSetChanged()
                    setState(SearchResultStates.OK)
                    hideKeyboard()
                } else{
                    searchDebounce()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        binding.btnRefreshSearch.setOnClickListener {
            searchTrack()
        }

        binding.searchEditText.addTextChangedListener(simpleTextWatcher)

        binding.clearText.setOnClickListener {
            binding.searchEditText.setText(R.string.empty_string)
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            setState(SearchResultStates.OK)
            hideKeyboard()
            binding.searchEditText.clearFocus()
        }

        binding.btnClearTrackHistory.setOnClickListener{
            tracksHistoryInteractor.clearHistory()
            trackHistoryAdapter.notifyDataSetChanged()
            binding.llTrackHistory.visibility = View.GONE
            hideKeyboard()
        }

        binding.searchEditText.setOnFocusChangeListener { _, _ ->
            changeTrackHistoryVisibility()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(EDIT_TEXT_KEY, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        binding.searchEditText.setText(savedInstanceState.getString(EDIT_TEXT_KEY))
    }

    private fun changeTrackHistoryVisibility(){
        binding.llTrackHistory.visibility =
            if (binding.searchEditText.hasFocus() && binding.searchEditText.text.isEmpty() && tracksHistoryInteractor.getHistory().size != 0)
                View.VISIBLE
            else View.GONE
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private var isClickAllowed = true

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed

        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }

        return current
    }

    private fun saveTrackAndOpenAudioPlayer(track: Track) {
        if (!clickDebounce())
            return

        tracksHistoryInteractor.updateHistory(track)
        trackHistoryAdapter.notifyDataSetChanged()
        openAudioPlayer(track, true)
    }

    private fun openAudioPlayer(track: Track, isAfterSave: Boolean = false){
        if (!clickDebounce() && !isAfterSave)
            return

        val intent: Intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra(TRACK_KEY, Gson().toJson(track))
        startActivity(intent)
    }

    private fun searchTrack() {
        binding.progressBar.visibility = View.VISIBLE

        tracksSearchInteractor.searchTracks(binding.searchEditText.getText().toString().trim(),
            object : TracksSearchInteractor.TrackConsumer {
                override fun consume(foundTracks: List<Track>?) {
                    handler.post { handleSearchResult(foundTracks) }
                }
            })
    }

    private fun handleSearchResult(foundTracks: List<Track>?) {
        tracks.clear()

        if (foundTracks == null) {
            setState(SearchResultStates.ERROR)
        } else{
            if (foundTracks.isNotEmpty()) {
                tracks.addAll(foundTracks)
                setState(SearchResultStates.OK)
            } else {
                setState(SearchResultStates.NOT_FUND)
            }
        }

        trackAdapter.notifyDataSetChanged()
    }

    private fun setState(state: SearchResultStates) {
        binding.progressBar.visibility = View.GONE
        binding.rvTrack.visibility = getVisibilityState(state == SearchResultStates.OK)
        binding.llNotFoundSearch.visibility = getVisibilityState(state == SearchResultStates.NOT_FUND)
        binding.llErrorSearch.visibility = getVisibilityState(state == SearchResultStates.ERROR)
    }

    private fun getVisibilityState(isVisible: Boolean): Int {
        return if (isVisible) View.VISIBLE else View.GONE
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    companion object {
        const val EDIT_TEXT_KEY: String = "EDIT_TEXT_KEY"
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}