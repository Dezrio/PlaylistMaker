package com.example.playlistmaker.compose

import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

val Black = Color(0xFF000000)
val White = Color(0xFFFFFFFF)
val Gray900 = Color(0xFF1A1B22)
val Gray400 = Color(0xFFAEAFB4)
val Gray50 = Color(0xFFE6E8EB)
val Blue700 = Color(0xFF3772E7)
val Blue200 = Color(0xFF9FBBF3)
val Red300 = Color(0xFFF56B6C)

val LightColorScheme = lightColorScheme(
    primary = White,
    onPrimary = Gray900,
    secondary = Gray50,
    onSecondary = Gray400,
    tertiary = Gray400,
    onTertiary = Gray400,
    background = Color.White,
    surface = Color.White,
    onSurface = Gray900
)

val DarkColorScheme = darkColorScheme(
    primary = Gray900,
    onPrimary = White,
    secondary = White,
    onSecondary = Gray900,
    tertiary = White,
    onTertiary = White,
    background = Color(0xFF121212),
    surface = Color(0xFF121212),
    onSurface = White
)