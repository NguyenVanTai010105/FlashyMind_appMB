package com.example.flashcardapp.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import com.example.flashcardapp.ui.WordModelViewModel
import com.example.flashcardapp.ui.theme.*
import com.example.flashcardapp.ui.components.*

@Composable
fun DashboardScreen(
    viewModel: WordModelViewModel,
    onStudyDue: (List<com.example.flashcardapp.modal.WordModel>) -> Unit,
    onNavigateToAddSet: () -> Unit,
    onSetSelected: (String) -> Unit
) {
    val stats by viewModel.userStats.collectAsState()
    val allSets by viewModel.allSets.collectAsState()
    val cardsDueToday by viewModel.cardsDueToday.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserStats()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // A. HEADER (Sát trên cùng)
            Text(
                text = "FlashyMind",
                style = Typography.displayLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 10.dp),
                textAlign = TextAlign.Center,
                color = SoftText
            )

            // B. STATS ROW (Sát dưới tiêu đề)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardModuleCard(
                    title = "Ngày học",
                    subtitle = "Chuỗi: ${stats?.streak ?: 0}",
                    icon = Icons.Default.Star,
                    iconColor = PastelRed,
                    modifier = Modifier.weight(1f),
                    onClick = { /* Streak details */ }
                )
                
                DashboardModuleCard(
                    title = "Ôn tập",
                    subtitle = "${cardsDueToday.size} thẻ",
                    icon = Icons.Default.Refresh,
                    iconColor = PastelBlue,
                    modifier = Modifier.weight(1f),
                    onClick = { 
                        if (cardsDueToday.isNotEmpty()) onStudyDue(cardsDueToday)
                    }
                )
            }
            
            Spacer(Modifier.height(24.dp))

            // RECENT DECKS (To, dài, sát mép 2 bên)
            Text(
                "Bộ thẻ gần đây",
                style = Typography.displaySmall,
                color = SoftText,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // DECKS SECTION
                items(allSets.take(3)) { set ->
                    SwipeableItemContainer(
                        onAction = { onSetSelected(set.title) }, // Vuốt để xem chi tiết hoặc sửa
                        actionColor = LiquidBlue.copy(alpha = 0.4f)
                    ) {
                        PastelGlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalPadding = 12.dp,
                            verticalPadding = 6.dp,
                            cornerRadius = 16.dp,
                            onClick = { onSetSelected(set.title) }
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(45.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(PastelPurple.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.CollectionsBookmark, null, tint = PastelPurple)
                                }
                                Spacer(Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(set.title, style = Typography.displaySmall, color = SoftText)
                                    Text("${set.description}", style = Typography.bodySmall, color = SoftText.copy(alpha = 0.5f))
                                }
                            }
                        }
                    }
                }

                // SECTION RECENT CARDS ĐÃ ĐƯỢC GỠ BỎ THEO YÊU CẦU
            }
        }
    }
}

@Composable
fun DashboardModuleCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    PastelGlassCard(
        modifier = modifier.height(130.dp),
        cornerRadius = 24.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = iconColor, modifier = Modifier.size(22.dp))
                }
                // Icon điều hướng đã gỡ bỏ theo yêu cầu tối giản
            }
            
            Column {
                Text(title, style = Typography.bodyLarge, fontWeight = FontWeight.Bold, color = SoftText)
                Text(subtitle, style = Typography.labelSmall, color = SoftText.copy(alpha = 0.6f))
            }
        }
    }
}
