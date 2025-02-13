package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
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
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.curs_uri))
            startActivity(shareIntent)
        }

        binding.support.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SENDTO)
            sendIntent.data = Uri.parse("mailto:")
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
            sendIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message))
            startActivity(sendIntent)
        }

        binding.arrowForward.setOnClickListener {
            val offerIntent = Intent(Intent.ACTION_VIEW)
            offerIntent.data = Uri.parse(getString(R.string.offer_uri))
            startActivity(offerIntent)
        }

        binding.settingsArrowBack.setOnClickListener {
            finish()
        }

        binding.smThemeSwitch.isChecked = (applicationContext as App).isDarkTheme()
        binding.smThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
        }
    }
}