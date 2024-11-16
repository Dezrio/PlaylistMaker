package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.searchBtn).setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Выполнение поиска...", Toast.LENGTH_SHORT).show()
            }
        })

        findViewById<Button>(R.id.libraryBtn).setOnClickListener {
            Toast.makeText(this@MainActivity, "Открытие мидиатеки", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.settingsBtn).setOnClickListener {
            Toast.makeText(this@MainActivity, "Настройка", Toast.LENGTH_SHORT).show()
        }
    }
}