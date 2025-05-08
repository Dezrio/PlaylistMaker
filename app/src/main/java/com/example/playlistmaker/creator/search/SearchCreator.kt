package com.example.playlistmaker.creator.search

import com.example.playlistmaker.data.search.TracksSearchRepositoryImpl
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.data.search.network.TracksAPIService
import com.example.playlistmaker.domain.search.api.interactor.TracksSearchInteractor
import com.example.playlistmaker.domain.search.api.repository.TracksSearchRepository
import com.example.playlistmaker.domain.search.impl.TracksSearchInteractorImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SearchCreator {
    private const val BASE_URL_ITUNES = "https://itunes.apple.com"

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_ITUNES)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getItunesApiService(): TracksAPIService {
        return getRetrofit().create(TracksAPIService::class.java)
    }

    private fun getTracksRepository(): TracksSearchRepository {
        return TracksSearchRepositoryImpl(RetrofitNetworkClient(getItunesApiService()))
    }

    fun provideTracksSearchInteractor(): TracksSearchInteractor {
        return TracksSearchInteractorImpl(getTracksRepository())
    }
}