package com.example.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.settings.SettingsCreator
import com.example.playlistmaker.creator.sharing.LinkManagerCreator
import com.example.playlistmaker.domain.settings.api.interactor.SettingsInteractor
import com.example.playlistmaker.domain.sharing.api.interactor.LinkManagerInteractor
import com.example.playlistmaker.util.SingleEventLiveData

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val linkManagerInteractor: LinkManagerInteractor
) : ViewModel() {
    private val themeLiveData = SingleEventLiveData<Boolean>()

    init {
        themeLiveData.value = settingsInteractor.isDarkTheme()
    }

    fun getThemeLiveData(): LiveData<Boolean> = themeLiveData

    fun switchTheme(isDarkTheme: Boolean) {
        settingsInteractor.switchTheme(isDarkTheme)
    }

    fun shareLink(shareAppLink: String) {
        linkManagerInteractor.shareLink(shareAppLink)
    }

    fun sendEmail(subject: String, text: String, email: Array<String>) {
        linkManagerInteractor.sendEmail(subject, text, email)
    }

    fun openLink(url: String) {
        linkManagerInteractor.openLink(url)
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(
                    SettingsCreator.provideSettingsInteractor(),
                    LinkManagerCreator.provideLinkManagerInteractor()
                )
            }
        }
    }
}