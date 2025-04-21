package com.example.playlistmaker.creator

import android.app.Application
import com.example.playlistmaker.data.LinkManagerInteractorImpl
import com.example.playlistmaker.domain.api.interactor.LinkManagerInteractor

object LinkManagerCreator {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    fun provideLinkManagerInteractor(): LinkManagerInteractor {
        return LinkManagerInteractorImpl(application)
    }
}