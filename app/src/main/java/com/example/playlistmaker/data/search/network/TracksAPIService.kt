package com.example.playlistmaker.data.search.network

import com.example.playlistmaker.data.search.dto.TracksSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TracksAPIService {
    @GET("/search?entity=song")
    suspend fun search(@Query("term") trackName: String): TracksSearchResponse
}