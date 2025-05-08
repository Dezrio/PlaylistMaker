package com.example.playlistmaker.di

import android.app.Application.MODE_PRIVATE
import android.media.MediaPlayer
import com.example.playlistmaker.data.search.NetworkClient
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.data.search.network.TracksAPIService
import com.example.playlistmaker.ui.App.Companion.DI_SHARED_PREFS_HISTORY
import com.example.playlistmaker.ui.App.Companion.DI_SHARED_PREFS_SETTINGS
import com.google.gson.Gson
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL_ITUNES = "https://itunes.apple.com"
private const val HISTORY_SHARED_PREFERENCES_FILE = "HISTORY_SHARED_PREFERENCES_FILE"
private const val SHARED_PREFERENCES_FILE = "SHARED_PREFERENCES_FILE"

val DataModule = module {
    factory {
        Gson()
    }

    factory {
        MediaPlayer()
    }

    single<TracksAPIService> {
        Retrofit.Builder()
            .baseUrl(BASE_URL_ITUNES)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TracksAPIService::class.java)
    }

    single(named(DI_SHARED_PREFS_HISTORY)) {
        androidApplication().getSharedPreferences(HISTORY_SHARED_PREFERENCES_FILE, MODE_PRIVATE)
    }

    single(named(DI_SHARED_PREFS_SETTINGS)) {
        androidApplication().getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }
}