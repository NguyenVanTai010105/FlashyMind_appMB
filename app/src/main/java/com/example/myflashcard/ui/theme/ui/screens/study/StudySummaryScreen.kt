package com.example.flashcardapp.ui.screens.study

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcardapp.ui.theme.*
import com.example.flashcardapp.ui.components.*

@Composable
fun StudySummaryScreen(
    knownCount: Int,
    incorrectCount: Int,
    onClose: () -> Unit,
    onContinueReview: () -> Unit
) {
    GlobalGlassBackground {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            PastelIconButton(
                icon = { Icon(Icons.Default.Close, contentDescription = "Close", tint = SoftText) },
                onClick = onClose,
                modifier = Modifier.align(Alignment.TopStart).padding(24.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = PastelBlue,
                    modifier = Modifier.size(120.dp)
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Hoàn thành buổi học!",
                    color = SoftText,
                    style = Typography.displayMedium,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Bạn đang làm rất tốt, hãy duy trì phong độ nhé!",
                    color = SoftText.copy(alpha = 0.6f),
                    style = Typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(Modifier.height(48.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PastelGlassCard(modifier = Modifier.weight(1f)) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("CHÍNH XÁC", style = Typography.labelSmall, color = PastelGreen)
                            Text("$knownCount", style = Typography.displayLarge, color = SoftText)
                        }
                    }

                    PastelGlassCard(modifier = Modifier.weight(1f)) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("CẦN ÔN LẠI", style = Typography.labelSmall, color = PastelRed)
                            Text("$incorrectCount", style = Typography.displayLarge, color = SoftText)
                        }
                    }
                }

                Spacer(Modifier.height(64.dp))

                PastelButton(
                    text = "ÔN TẬP LẠI",
                    onClick = onContinueReview,
                    containerColor = PastelBlue
                )
                
                Spacer(Modifier.height(16.dp))
                
                TextButton(onClick = onClose) {
                    Text("QUAY VỀ TRANG CHỦ", color = SoftText.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
