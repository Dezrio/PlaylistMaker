package com.example.playlistmaker.creator.sharing

import android.app.Application
import com.example.playlistmaker.data.sharing.LinkManagerInteractorImpl
import com.example.playlistmaker.domain.sharing.api.interactor.LinkManagerInteractor

object LinkManagerCreator {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        LinkManagerCreator.application = application
    }

    fun provideLinkManagerInteractor(): LinkManagerInteractor {
        return LinkManagerInteractorImpl(application)
    }
}