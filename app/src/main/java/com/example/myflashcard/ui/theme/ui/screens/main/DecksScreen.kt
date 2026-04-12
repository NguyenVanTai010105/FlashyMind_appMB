package com.example.flashcardapp.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import com.example.flashcardapp.modal.FolderModal
import com.example.flashcardapp.modal.WordModel
import com.example.flashcardapp.ui.WordModelViewModel
import com.example.flashcardapp.ui.theme.*
import com.example.flashcardapp.ui.components.*

@Composable
fun DecksScreen(
    viewModel: WordModelViewModel,
    onNavigateToAddSet: () -> Unit,
    onSetSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    val allSets by viewModel.filteredSets.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Spacer(Modifier.height(20.dp))

            // HEADER (Đồng bộ phong cách hiện đại)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PastelIconButton(
                    icon = { Icon(Icons.Default.ArrowBack, null, tint = SoftText, modifier = Modifier.size(24.dp)) },
                    onClick = onBack
                )
                
                Spacer(Modifier.width(16.dp))

                Text(
                    "THƯ VIỆN",
                    style = Typography.displayLarge,
                    color = SoftText,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                
                PastelIconButton(
                    icon = { Icon(Icons.Default.Add, null, tint = SoftText, modifier = Modifier.size(24.dp)) },
                    onClick = onNavigateToAddSet,
                    containerColor = PastelBlue.copy(alpha = 0.2f)
                )
            }

            Spacer(Modifier.height(24.dp))

            // SEARCH BAR (Liquid Glassy - Bo tròn nhẹ nhàng)
            PastelGlassCard(
                modifier = Modifier.fillMaxWidth(),
                horizontalPadding = 12.dp,
                verticalPadding = 4.dp,
                cornerRadius = 20.dp
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Tìm kiếm bộ thẻ...", color = SoftText.copy(alpha = 0.3f)) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = LiquidBlue) },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = SoftText,
                        unfocusedTextColor = SoftText,
                        focusedContainerColor = GlassWhiteLight,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = LiquidBlue,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(16.dp))

            // DECK LIST (San sát nhau, to dài ra 2 mép)
            var deckToDelete by remember { mutableStateOf<com.example.flashcardapp.modal.FolderModal?>(null) }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(1.dp), // San sát nhau
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(allSets) { set ->
                    SwipeableItemContainer(
                        onAction = { deckToDelete = set },
                        actionColor = PastelRed.copy(alpha = 0.6f)
                    ) {
                        PastelGlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalPadding = 12.dp,
                            verticalPadding = 6.dp,
                            cornerRadius = 16.dp,
                            onClick = { onSetSelected(set.title) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp, horizontal = 24.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(LiquidBlue.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Style, null, tint = LiquidBlue, modifier = Modifier.size(28.dp))
                                }
                                Spacer(Modifier.width(20.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(set.title, style = Typography.displaySmall, color = SoftText)
                                    if (set.description.isNotBlank()) {
                                        Text(set.description, style = Typography.bodySmall, color = SoftText.copy(alpha = 0.5f), maxLines = 1)
                                    }
                                }
                                // Icon đã được gỡ bỏ để tối giản giao diện
                            }
                        }
                    }
                }
            }

            // CONFIRM DELETE DIALOG
            if (deckToDelete != null) {
                AlertDialog(
                    onDismissRequest = { deckToDelete = null },
                    containerColor = GlassWhiteHeavy,
                    modifier = Modifier.border(1.dp, GlassBorder, RoundedCornerShape(28.dp)),
                    title = { Text("Xác nhận xóa", style = Typography.headlineMedium, color = SoftText) },
                    text = { Text("Bạn có chắc chắn muốn xóa bộ thẻ '${deckToDelete?.title}' không?", style = Typography.bodyLarge, color = SoftText.copy(alpha = 0.8f)) },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.deleteSet(deckToDelete!!)
                                deckToDelete = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PastelRed)
                        ) {
                            Text("XÓA", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { deckToDelete = null }) {
                            Text("HỦY", color = SoftText.copy(alpha = 0.6f))
                        }
                    }
                )
            }
        }
    }
}
