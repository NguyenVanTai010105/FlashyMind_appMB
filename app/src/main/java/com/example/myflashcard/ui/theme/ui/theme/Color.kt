package com.example.flashcardapp.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Pastel Glassmorphism Palette
val PastelBlue = Color(0xFF6A8DFF)
val PastelBlueLight = Color(0xFF8EC5FC)
val PastelMint = Color(0xFFA1FFCE)
val PastelPurple = Color(0xFFE0C3FC)
val PastelYellow = Color(0xFFFDEB71)
val SoftPink = Color(0xFFFF9A9E)

// Soft UI Colors
val SoftWhite = Color(0xFFF8F9FA)
val SoftGray = Color(0xFFE9ECEF)
val SoftText = Color(0xFFF8F9FA) // Soft White for dark background contrast

// Glassmorphism Core (Liquid Style)
val GlassWhite = Color.White.copy(alpha = 0.20f)
val GlassWhiteHeavy = Color.White.copy(alpha = 0.40f) // Đục hơn đáng kể
val GlassWhiteLight = Color.White.copy(alpha = 0.12f)
val GlassBorder = Color.White.copy(alpha = 0.40f)
val DeepGlass = Color(0xFF1E1E2E).copy(alpha = 0.95f) // Đậm đặc để che phủ hoàn toàn nền
val SoftShadow = Color(0x40000000) // 25% Black

// Liquid Colors
val LiquidPink = Color(0xFFFF4E91)
val LiquidBlue = Color(0xFF00D2FF)
val LiquidPurple = Color(0xFF9D50BB)
val SelectedYellow = Color(0xFFFFD700)
val SelectedYellowGlass = Color(0xFFFFD700).copy(alpha = 0.35f)

val LiquidGradient = Brush.linearGradient(
    colors = listOf(LiquidPink, LiquidPurple, LiquidBlue)
)

// Evaluation Pastel Colors
val PastelRed = Color(0xFFFF7675)
val PastelGreen = Color(0xFF55E6C1)

// Main Background Gradient (Deep Liquid Theme)
val MainAppGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF0F0C29), // Deep Space
        Color(0xFF302B63), // Royal Blue
        Color(0xFF24243E)  // Navy
    )
)

// Card Gradients
val YellowCardGradient = Brush.linearGradient(listOf(Color(0xFFFDEB71), Color(0xFFF8D800)))
val MintCardGradient = Brush.linearGradient(listOf(Color(0xFFA1FFCE), Color(0xFF2AF598)))
val PurpleCardGradient = Brush.linearGradient(listOf(Color(0xFFE0C3FC), Color(0xFF8EC5FC)))

val GlassyCardBrush = Brush.verticalGradient(
    listOf(Color.White.copy(alpha = 0.6f), Color.White.copy(alpha = 0.2f))
)