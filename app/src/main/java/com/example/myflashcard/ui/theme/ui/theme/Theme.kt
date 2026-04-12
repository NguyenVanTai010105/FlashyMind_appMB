package com.example.flashcardapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun WordModelAppTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme(
        primary = PastelBlue,
        secondary = PastelPurple,
        tertiary = SoftPink,
        background = Color.Transparent, 
        surface = Color.Transparent,
        onSurface = SoftText,
        onPrimary = Color.White
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}