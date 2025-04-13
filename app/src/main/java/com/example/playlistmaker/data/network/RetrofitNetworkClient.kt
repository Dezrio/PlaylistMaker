package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.network.consts.SearchResponseStates.BAD_REQUEST
import com.example.playlistmaker.data.network.consts.SearchResponseStates.INTERNAL_SERVER_ERROR

class RetrofitNetworkClient(private val tracksService: TracksAPIService) : NetworkClient {
    override fun doRequest(dto: Any): Response {
        if (dto is TracksSearchRequest) {
            val response = kotlin.runCatching {
                    tracksService.search(dto.trackName).execute()
                }.getOrNull()

            if (response == null)
                return Response().apply { resultCode = INTERNAL_SERVER_ERROR }

            val body = response.body() ?: Response()

            return body.apply { resultCode = response.code() }
        } else {
            return Response().apply { resultCode = BAD_REQUEST }
        }
    }
}