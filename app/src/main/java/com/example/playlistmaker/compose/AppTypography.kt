package com.example.playlistmaker.compose

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun appTypography(colorScheme: ColorScheme): Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = AppFont.YsDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        color = colorScheme.onPrimary
    ),
    displayMedium = TextStyle(
        fontFamily = AppFont.YsDisplay,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        color = colorScheme.onPrimary
    ),
    displaySmall = TextStyle(
        fontFamily = AppFont.YsDisplay,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        color = colorScheme.onPrimary
    ),
    headlineLarge = TextStyle(
        fontFamily = AppFont.YsDisplay,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        color = colorScheme.onPrimary
    ),
    headlineMedium = TextStyle(
        fontFamily = AppFont.YsDisplay,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        color = colorScheme.onPrimary
    ),
    headlineSmall = TextStyle(
        fontFamily = AppFont.YsDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        color = colorScheme.onPrimary
    ),
    titleLarge = TextStyle(
        fontFamily = AppFont.YsDisplay,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        color = colorScheme.onPrimary
    ),
    titleMedium = TextStyle(
        fontFamily = AppFont.YsDisplay,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        color = colorScheme.surface
    ),
    titleSmall = TextStyle(
        fontFamily = AppFont.YsDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        color = colorScheme.onTertiary
    ),
    bodyLarge = TextStyle(
        fontFamily = AppFont.YsDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = colorScheme.onPrimary
    ),
    bodyMedium = TextStyle(
        fontFamily = AppFont.YsDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = colorScheme.onSecondary
    ),
    bodySmall = TextStyle(
        fontFamily = AppFont.YsDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        color = colorScheme.onSecondary
    ),
    labelLarge = TextStyle(
        fontFamily = AppFont.YsDisplay,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        color = colorScheme.primary
    ),
    labelMedium = TextStyle(
        fontFamily = AppFont.YsDisplay,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        color = colorScheme.primary
    ),
    labelSmall = TextStyle(
        fontFamily = AppFont.YsDisplay,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        color = colorScheme.onTertiary
    )
)