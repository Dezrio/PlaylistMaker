package com.example.playlistmaker.data.sharing

import android.app.Application
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.domain.sharing.api.interactor.LinkManagerInteractor

class LinkManagerInteractorImpl(private val application: Application) : LinkManagerInteractor {

    override fun sendEmail(subject: String, text: String, email: Array<String>) {
        val sendIntent = Intent(Intent.ACTION_SENDTO)
        sendIntent.data = Uri.parse("mailto:")
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        sendIntent.putExtra(Intent.EXTRA_TEXT, text)
        sendIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(sendIntent)
    }

    override fun shareLink(link: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
        shareIntent.putExtra(Intent.EXTRA_TEXT, link)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(shareIntent)
    }

    override fun openLink(link: String) {
        val offerIntent = Intent(Intent.ACTION_VIEW)
        offerIntent.data = Uri.parse(link)
        offerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(offerIntent)
    }
}