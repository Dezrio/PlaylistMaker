package com.example.playlistmaker.data.search.network

import com.example.playlistmaker.data.search.NetworkClient
import com.example.playlistmaker.data.search.dto.Response
import com.example.playlistmaker.data.search.dto.TracksSearchRequest
import com.example.playlistmaker.data.search.network.consts.SearchResponseStates.BAD_REQUEST
import com.example.playlistmaker.data.search.network.consts.SearchResponseStates.INTERNAL_SERVER_ERROR
import com.example.playlistmaker.data.search.network.consts.SearchResponseStates.SUCCESS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(private val tracksService: TracksAPIService) : NetworkClient {
    override suspend fun doRequest(dto: Any): Response {
        if (dto !is TracksSearchRequest)
            return Response().apply { resultCode = BAD_REQUEST }

        return withContext(Dispatchers.IO){
            try{
                val response = tracksService.search(dto.trackName)

                response.apply { resultCode = SUCCESS }
            }catch (e: Throwable){
                Response().apply { resultCode = INTERNAL_SERVER_ERROR }
            }
        }
    }
}