package com.example.playlistmaker.ui.settings.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {
    private val viewModel: SettingsViewModel by viewModels<SettingsViewModel> {
        SettingsViewModel.getViewModelFactory()
    }

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

        binding.settingsArrowBack.setOnClickListener {
            finish()
        }

        binding.support.setOnClickListener {
            viewModel.sendEmail(
                getString(R.string.support_subject),
                getString(R.string.support_message),
                arrayOf(getString(R.string.support_email)))
        }

        binding.arrowForward.setOnClickListener {
            viewModel.openLink(getString(R.string.offer_uri))
        }

        binding.share.setOnClickListener {
            viewModel.shareLink(getString(R.string.curs_uri))
        }

        viewModel.getThemeLiveData().observe(this) { isDarkTheme ->
            binding.smThemeSwitch.isChecked = isDarkTheme
        }

        binding.smThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
        }
    }
}