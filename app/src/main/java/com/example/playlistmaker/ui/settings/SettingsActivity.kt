package com.example.playlistmaker.ui.settings

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.LinkManagerCreator
import com.example.playlistmaker.creator.SettingsCreator
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private val settingsInteractorImpl = SettingsCreator.provideSettingsInteractor()
    private val linkManagerInteractorImpl = LinkManagerCreator.provideLinkManagerInteractor()

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.share.setOnClickListener {
            linkManagerInteractorImpl.shareLink(getString(R.string.curs_uri))
        }

        binding.support.setOnClickListener {
            linkManagerInteractorImpl.sendEmail(
                getString(R.string.support_subject),
                getString(R.string.support_message),
                arrayOf(getString(R.string.support_email)))
        }

        binding.arrowForward.setOnClickListener {
            linkManagerInteractorImpl.openLink(getString(R.string.offer_uri))
        }

        binding.settingsArrowBack.setOnClickListener {
            finish()
        }

        binding.smThemeSwitch.isChecked = settingsInteractorImpl.isDarkTheme()
        binding.smThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsInteractorImpl.switchTheme(isChecked)
        }
    }
}