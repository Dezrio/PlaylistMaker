package com.example.playlistmaker.compose

import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

val Black = Color(0xFF000000)
val White = Color(0xFFFFFFFF)
val GrayMain = Color(0xFF1A1B22)
val GraySecond = Color(0xFFAEAFB4)
val GrayThird = Color(0xFFE6E8EB)

val LightColorScheme = lightColorScheme(
    primary = White,
    onPrimary = GrayMain,
    secondary = GrayThird,
    onSecondary = GraySecond,
    tertiary = GraySecond,
    onTertiary = GraySecond,
    background = Color.White,
    onBackground = Color.Black,
    surface = GrayMain,
    onSurface = GrayMain
)

val DarkColorScheme = darkColorScheme(
    primary = GrayMain,
    onPrimary = White,
    secondary = White,
    onSecondary = GrayMain,
    tertiary = White,
    onTertiary = White,
    background = Color(0xFF121212),
    onBackground = White,
    surface = Black,
    onSurface = White
)