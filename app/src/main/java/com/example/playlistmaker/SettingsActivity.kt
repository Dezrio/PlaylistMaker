package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.share).setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.curs_uri))
            startActivity(shareIntent)
        }

        findViewById<ImageView>(R.id.support).setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SENDTO)
            sendIntent.data = Uri.parse("mailto:")
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
            sendIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message))
            startActivity(sendIntent)
        }

        findViewById<ImageView>(R.id.arrow_forward).setOnClickListener {
            val offerIntent = Intent(Intent.ACTION_VIEW)
            offerIntent.data = Uri.parse(getString(R.string.offer_uri))
            startActivity(offerIntent)
        }

        findViewById<ImageView>(R.id.settings_arrow_back).setOnClickListener {
            finish()
        }
    }
}