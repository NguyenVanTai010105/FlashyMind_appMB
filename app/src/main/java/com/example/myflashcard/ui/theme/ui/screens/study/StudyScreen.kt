package com.example.flashcardapp.ui.screens.study

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import com.example.flashcardapp.modal.WordModel
import com.example.flashcardapp.ui.WordModelViewModel
import com.example.flashcardapp.ui.components.*
import com.example.flashcardapp.ui.theme.*

@Composable
fun StudyScreen(
    studyList: List<WordModel>,
    viewModel: WordModelViewModel,
    tts: TextToSpeech?,
    onBack: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }
    var isFlipped by remember { mutableStateOf(false) }
    
    val currentCard = if (studyList.isNotEmpty() && currentIndex < studyList.size) studyList[currentIndex] else null

    // Reset flipped state when card changes
    LaunchedEffect(currentIndex) { isFlipped = false }

    if (isFinished || currentCard == null) {
        // ... (Completion Screen)
        GlobalGlassBackground {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                PastelGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Notifications, null, tint = PastelPurple, modifier = Modifier.size(80.dp))
                        Spacer(Modifier.height(24.dp))
                        Text("TUYỆT VỜI!", style = Typography.displayLarge, color = SoftText)
                        Text("Bạn đã hoàn thành phiên học hôm nay.", style = Typography.bodyLarge, textAlign = TextAlign.Center)
                        
                        Spacer(Modifier.height(32.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${studyList.size}", style = Typography.displayMedium, color = PastelBlue)
                                Text("Số thẻ", style = Typography.labelSmall)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("100%", style = Typography.displayMedium, color = PastelGreen)
                                Text("Hoàn thành", style = Typography.labelSmall)
                            }
                        }
                    }
                }
                
                Spacer(Modifier.height(48.dp))
                
                PastelButton(
                    text = "TIẾP TỤC",
                    onClick = onBack,
                    containerColor = PastelBlue
                )
            }
        }
        return
    }

    GlobalGlassBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // HEADER BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(GlassWhite)
                ) {
                    Icon(Icons.Default.Close, null, tint = SoftText, modifier = Modifier.size(24.dp))
                }
                
                Spacer(Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text("ĐANG ÔN TẬP", style = Typography.displaySmall, color = SoftText)
                    Text("${currentIndex + 1}/${studyList.size}", style = Typography.labelSmall, color = PastelBlue)
                }
            }

            Spacer(Modifier.height(20.dp))

            // FLASHCARD STACK
            StackedFlashcard(
                word = currentCard.word,
                meaning = currentCard.meaning,
                imageUri = currentCard.imageUri,
                onFlip = { isFlipped = !isFlipped },
                isFlipped = isFlipped,
                onSpeak = {
                    tts?.speak(currentCard.word, TextToSpeech.QUEUE_FLUSH, null, null)
                },
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.height(40.dp))

            // EVALUATION BUTTONS (Show only when flipped)
            if (isFlipped) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 48.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ReviewButton(
                        label = "Quên",
                        color = PastelRed,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.updateCardStats(currentCard, 0)
                            if (currentIndex < studyList.size - 1) currentIndex++ else isFinished = true
                        }
                    )
                    
                    ReviewButton(
                        label = "Khó",
                        color = PastelPurple,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.updateCardStats(currentCard, 3)
                            if (currentIndex < studyList.size - 1) currentIndex++ else isFinished = true
                        }
                    )
                    
                    ReviewButton(
                        label = "Dễ",
                        color = PastelGreen,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.updateCardStats(currentCard, 5)
                            if (currentIndex < studyList.size - 1) currentIndex++ else isFinished = true
                        }
                    )
                }
            } else {
                Text(
                    "Chạm vào thẻ để xem nghĩa",
                    style = Typography.bodyMedium,
                    color = SoftText.copy(alpha = 0.4f),
                    modifier = Modifier.padding(bottom = 64.dp)
                )
            }
        }
    }
}

@Composable
fun ReviewButton(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = color.copy(alpha = 0.3f))
            .clip(RoundedCornerShape(20.dp))
            .background(color)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.White,
            style = Typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}
