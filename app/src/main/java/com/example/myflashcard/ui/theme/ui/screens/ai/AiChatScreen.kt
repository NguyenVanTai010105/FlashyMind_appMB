package com.example.flashcardapp.ui.screens.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcardapp.modal.ChatMessage
import com.example.flashcardapp.ui.WordModelViewModel
import com.example.flashcardapp.ui.theme.*
import com.example.flashcardapp.ui.components.*

@Composable
fun AiChatScreen(
    viewModel: WordModelViewModel,
    onBack: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isTyping by viewModel.isAITyping.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(chatMessages.size, isTyping) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    GlobalGlassBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // HEADER (Đã đồng bộ)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PastelIconButton(
                    icon = { Icon(Icons.Default.ArrowBack, null, tint = SoftText) },
                    onClick = onBack
                )
                
                Spacer(Modifier.width(16.dp))

                Text(
                    text = "TRÒ CHUYỆN AI",
                    style = Typography.displayLarge,
                    color = SoftText
                )
            }

            // FULL SCREEN CHAT AREA
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(chatMessages) { chat ->
                    PastelChatMessage(chat)
                }
                
                if (isTyping) {
                    item {
                        Text(
                            "Flashy đang suy nghĩ...",
                            style = Typography.bodyMedium,
                            color = LiquidBlue,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }

            // INPUT AREA (Đã đồng bộ Liquid Glassy - Khớp 100% với Nav màn hình chính)
            PastelGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                cornerRadius = 32.dp,
                backgroundColor = GlassWhiteHeavy
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Hỏi tôi bất cứ điều gì...", color = SoftText.copy(alpha = 0.4f)) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = LiquidBlue,
                            focusedTextColor = SoftText
                        )
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(LiquidGradient)
                            .clickable {
                                if (messageText.isNotBlank()) {
                                    viewModel.sendChatMessage(messageText)
                                    messageText = ""
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Send, null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PastelChatMessage(chat: ChatMessage) {
    val isUser = chat.role == "user"
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.1f))
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (isUser) 20.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 20.dp
                    )
                )
                // CẬP NHẬT: Sử dụng hiệu ứng Kính mờ (Liquid Glassy) - Đục hơn theo yêu cầu
                .background(if (isUser) LiquidBlue.copy(alpha = 0.85f) else GlassWhiteHeavy)
                .border(
                    1.dp, 
                    if (isUser) Color.White.copy(alpha = 0.2f) else GlassBorder,
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (isUser) 20.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 20.dp
                    )
                )
                .padding(16.dp)
        ) {
            Text(
                text = chat.message,
                color = if (isUser) Color.White else Color(0xFF2D3436),
                style = Typography.bodyLarge
            )
        }
    }
}
