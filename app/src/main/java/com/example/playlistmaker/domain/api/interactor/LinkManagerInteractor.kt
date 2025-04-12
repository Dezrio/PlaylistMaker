package com.example.playlistmaker.domain.api.interactor

interface LinkManagerInteractor {
    fun sendEmail(subject: String, text: String, email: Array<String>)

    fun shareLink(link: String)

    fun openLink(link: String)
}