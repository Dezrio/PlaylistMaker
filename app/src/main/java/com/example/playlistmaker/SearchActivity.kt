package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.App.Companion.TRACK_KEY
import com.example.playlistmaker.bind.search.TrackAdapter
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.google.gson.Gson
import dataclasses.Track
import dataclasses.TracksResponse
import enums.SeachResultState
import interfaces.TracksAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private var searchText: String = ""
    private var tracks: MutableList<Track> = mutableListOf()
    private lateinit var searchEditText: EditText
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var trackHistoryAdapter: TrackAdapter
    private lateinit var searchHistoryHandler: SearchHistoryHandler
    private lateinit var retrofit: Retrofit
    private lateinit var tracksService: TracksAPI
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

        retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.track_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        tracksService = retrofit.create(TracksAPI::class.java)

        binding.settingsArrowBack.setOnClickListener {
            finish()
        }

        trackAdapter = TrackAdapter(tracks, ::saveTrackAndOpenAudioPlayer)
        binding.rvTrack.adapter = trackAdapter
        binding.rvTrack.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        searchHistoryHandler = SearchHistoryHandler((applicationContext as App).getSharedPreferences())
        trackHistoryAdapter = TrackAdapter(searchHistoryHandler.getSearchHistoryTracks(), ::openAudioPlayer)
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
                    setState(SeachResultState.OK)
                    hideKeyboard()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        binding.btnRefreshSearch.setOnClickListener {
            searchTrack()
        }

        binding.searchEditText.addTextChangedListener(simpleTextWatcher)

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.searchEditText.getText().isNullOrEmpty()){
                    setState(SeachResultState.OK)
                    changeTrackHistoryVisibility()
                    searchText  = getString(R.string.empty_string)
                    true
                }

                searchText = binding.searchEditText.getText().toString()

                searchTrack()

                true
            }

            false
        }

        binding.clearText.setOnClickListener {
            binding.searchEditText.setText(R.string.empty_string)
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            setState(SeachResultState.OK)
            hideKeyboard()
            binding.searchEditText.clearFocus()
        }

        binding.btnClearTrackHistory.setOnClickListener{
            searchHistoryHandler.clearSearchHistory()
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
            if (binding.searchEditText.hasFocus() && binding.searchEditText.text.isEmpty() && searchHistoryHandler.getSearchHistoryTracks().size != 0)
                View.VISIBLE
            else View.GONE
    }

    private fun saveTrackAndOpenAudioPlayer(track: Track) {
        searchHistoryHandler.saveTrack(track)
        trackHistoryAdapter.notifyDataSetChanged()
        openAudioPlayer(track)
    }

    private fun openAudioPlayer(track: Track){
        val intent: Intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra(TRACK_KEY, Gson().toJson(track))
        startActivity(intent)
    }

    private fun searchTrack(){
        tracksService.search(searchText).enqueue(object : Callback<TracksResponse> {
            override fun onResponse(call: Call<TracksResponse>,
                                    response: Response<TracksResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()?.results?.isNotEmpty() == true) {
                        tracks.clear()
                        tracks.addAll(response.body()?.results!!)
                        trackAdapter.notifyDataSetChanged()
                        setState(SeachResultState.OK)
                    } else {
                        tracks.clear()
                        trackAdapter.notifyDataSetChanged()
                        setState(SeachResultState.NOT_FUND)
                    }

                } else {
                    tracks.clear()
                    trackAdapter.notifyDataSetChanged()
                    setState(SeachResultState.ERROR)
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                setState(SeachResultState.ERROR)
            }
        })
    }

    private fun setState(state: SeachResultState) {
        binding.rvTrack.visibility = getVisibilityState(state == SeachResultState.OK)
        binding.llNotFoundSearch.visibility = getVisibilityState(state == SeachResultState.NOT_FUND)
        binding.llErrorSearch.visibility = getVisibilityState(state == SeachResultState.ERROR)
    }

    private fun getVisibilityState(isVisible: Boolean): Int {
        return if (isVisible) View.VISIBLE else View.GONE
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    companion object {
        const val EDIT_TEXT_KEY:String = "EDIT_TEXT_KEY"
    }
}