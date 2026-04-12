package com.example.flashcardapp.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcardapp.ui.WordModelViewModel
import com.example.flashcardapp.ui.theme.*
import com.example.flashcardapp.ui.components.*

@Composable
fun ProfileScreen(
    viewModel: WordModelViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // BACK BUTTON
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
                    Icon(Icons.Default.ArrowBack, null, tint = SoftText, modifier = Modifier.size(24.dp))
                }
                
                Spacer(Modifier.width(16.dp))

                Text(
                    "TRANG CÁ NHÂN",
                    style = Typography.displayLarge,
                    color = SoftText
                )
            }

            Spacer(Modifier.height(20.dp))

            // USER AVATAR (Pastel Style)
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(GlassWhite)
                    .border(2.dp, GlassBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    null,
                    tint = PastelBlue,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // ACCOUNT INFO
            Text(
                text = viewModel.loggedInEmail ?: "Người dùng",
                style = Typography.displayMedium,
                color = SoftText
            )
            
            Spacer(Modifier.height(48.dp))

            // MODULES
            Text(
                "LỊCH SỬ HỌC TẬP",
                style = Typography.displaySmall,
                color = SoftText,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 12.dp)
            )

            val studiedCards by viewModel.studiedCards.collectAsState()

            PastelGlassCard(
                modifier = Modifier.fillMaxWidth().weight(1f),
                cornerRadius = 24.dp
            ) {
                if (studiedCards.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Chưa có lịch sử học tập", style = Typography.bodyMedium, color = SoftText.copy(alpha = 0.5f))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(studiedCards) { card ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(15.dp))
                                    .background(Color.White.copy(alpha = 0.3f))
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(PastelBlue.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.List, null, tint = PastelBlue, modifier = Modifier.size(20.dp))
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(card.word, style = Typography.bodyLarge, fontWeight = FontWeight.Bold, color = SoftText)
                                    Text(card.meaning, style = Typography.labelSmall, color = SoftText.copy(alpha = 0.6f))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            
            // LOGOUT BUTTON
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .clip(RoundedCornerShape(34.dp))
                    .background(PastelRed.copy(alpha = 0.15f))
                    .border(1.dp, PastelRed.copy(alpha = 0.3f), RoundedCornerShape(34.dp))
                    .clickable { onLogout() },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Logout, null, tint = PastelRed, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "ĐĂNG XUẤT",
                        style = Typography.displaySmall,
                        color = PastelRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(Modifier.height(40.dp)) // Added space to lift the button
        }
    }
}

@Composable
fun ProfileListItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = SoftText.copy(alpha = 0.6f), modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Text(label, style = Typography.bodyLarge, color = SoftText)
        Spacer(Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, null, tint = SoftText.copy(alpha = 0.3f))
    }
}
