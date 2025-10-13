package com.example.playlistmaker.compose

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.playlistmaker.R

object AppFont {
    val YsDisplay = FontFamily(
        Font(R.font.ys_display_bold, FontWeight.Bold),
        Font(R.font.ys_display_medium, FontWeight.Medium),
        Font(R.font.ys_display_regular, FontWeight.Normal)
    )
}