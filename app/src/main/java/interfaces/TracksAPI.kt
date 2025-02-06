package interfaces

import dataclasses.TracksResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TracksAPI {
    @GET("/search")
    fun search(@Query("term") trakName: String): Call<TracksResponse>
}