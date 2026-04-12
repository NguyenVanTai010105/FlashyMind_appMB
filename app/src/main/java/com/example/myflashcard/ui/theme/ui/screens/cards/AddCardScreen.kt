package com.example.flashcardapp.ui.screens.cards

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flashcardapp.ui.WordModelViewModel
import com.example.flashcardapp.ui.theme.*
import com.example.flashcardapp.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardScreen(setTitle: String, viewModel: WordModelViewModel, onBack: () -> Unit) {
    var word by remember { mutableStateOf("") }
    var meaning by remember { mutableStateOf("") }
    val isTranslating by viewModel.isTranslating.collectAsState()
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    GlobalGlassBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("THÊM THẺ MỚI", style = Typography.displaySmall) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = SoftText
                    ),
                    navigationIcon = {
                        PastelIconButton(
                            icon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = SoftText) },
                            onClick = onBack,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    },
                    actions = {
                        val isInputValid = word.isNotBlank() && meaning.isNotBlank()
                        PastelIconButton(
                            icon = { Icon(Icons.Default.Check, "Save", tint = if (isInputValid) LiquidBlue else SoftText.copy(alpha = 0.2f)) },
                            onClick = {
                                if (isInputValid) {
                                    viewModel.addCard(word, meaning, setTitle, imageUri?.toString())
                                    onBack()
                                }
                            },
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                PastelGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        TextField(
                            value = word,
                            onValueChange = { word = it },
                            placeholder = { Text("Từ vựng / Thuật ngữ", color = SoftText.copy(alpha = 0.7f)) },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = SoftText,
                                unfocusedTextColor = SoftText
                            ),
                            trailingIcon = {
                                if (isTranslating) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = PastelBlue, strokeWidth = 2.dp)
                                } else {
                                    IconButton(onClick = { 
                                        if (word.isNotBlank()) viewModel.translateWord(word) { result -> meaning = result }
                                    }) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = "Dịch AI", tint = PastelBlue)
                                    }
                                }
                            }
                        )
                        
                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                        TextField(
                            value = meaning,
                            onValueChange = { meaning = it },
                            placeholder = { Text("Định nghĩa / Ý nghĩa", color = SoftText.copy(alpha = 0.7f)) },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = SoftText,
                                unfocusedTextColor = SoftText
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                // --- IMAGE SELECTOR ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(GlassWhite)
                        .border(1.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                        // Explicit "TAP TO CHANGE" Overlay
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .align(Alignment.BottomCenter)
                                .background(Color.Black.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PhotoCamera, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("CHẠM ĐỂ THAY ĐỔI", color = Color.White, style = Typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.AddPhotoAlternate, 
                                null, 
                                tint = SoftText.copy(alpha = 0.5f), 
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Thêm hình ảnh minh họa", 
                                color = SoftText.copy(alpha = 0.5f), 
                                style = Typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                // Đã gỡ bỏ thanh PastelButton ở đây theo yêu cầu
            }
        }
    }
}
