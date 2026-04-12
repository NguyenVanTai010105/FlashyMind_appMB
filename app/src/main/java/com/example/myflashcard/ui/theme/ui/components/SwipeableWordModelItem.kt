package com.example.flashcardapp.ui.components

import android.speech.tts.TextToSpeech
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flashcardapp.modal.WordModel
import com.example.flashcardapp.ui.theme.*

@Composable
fun SwipeableWordModelItem(card: WordModel, tts: TextToSpeech?) {
    var isFlipped by remember { mutableStateOf(false) }
    LaunchedEffect(card.word) { isFlipped = false }
    val rotation by animateFloatAsState(targetValue = if (isFlipped) 180f else 0f, animationSpec = tween(600), label = "cardFlip")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .graphicsLayer { 
                rotationY = rotation
                cameraDistance = 16f * density 
            }
            .clip(RoundedCornerShape(32.dp))
            .background(GlassWhite)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.4f), Color.White.copy(alpha = 0.05f))
                ),
                shape = RoundedCornerShape(32.dp)
            )
            .clickable { isFlipped = !isFlipped },
        contentAlignment = Alignment.Center
    ) {
        if (rotation <= 90f) {
            // FRONT
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (card.imageUri != null) {
                    AsyncImage(
                        model = card.imageUri,
                        contentDescription = "Card Image",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(20.dp)
                            .clip(RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                
                Text(
                    text = card.word, 
                    style = Typography.displayLarge, 
                    color = Color.White, 
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(24.dp)
                )

                Box(
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(GlassWhite)
                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                        .clickable { tts?.speak(card.word, TextToSpeech.QUEUE_FLUSH, null, null) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.VolumeUp, "Speak", tint = PastelBlue, modifier = Modifier.size(36.dp))
                }
            }
        } else {
            // BACK
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Dịch nghĩa",
                    style = Typography.headlineMedium,
                    color = PastelBlue,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Text(
                    text = card.meaning,
                    style = Typography.displayMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(Modifier.height(48.dp))
                
                Icon(
                    Icons.Default.VolumeUp, 
                    null, 
                    tint = Color.White.copy(alpha = 0.2f), 
                    modifier = Modifier.size(64.dp)
                )
            }
        }
    }
}
