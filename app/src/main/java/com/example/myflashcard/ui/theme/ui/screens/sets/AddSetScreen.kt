package com.example.flashcardapp.ui.screens.sets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.flashcardapp.ui.WordModelViewModel
import com.example.flashcardapp.ui.theme.*
import com.example.flashcardapp.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSetScreen(viewModel: WordModelViewModel, onBack: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    GlobalGlassBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("TẠO BỘ THẺ MỚI", style = Typography.displaySmall) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = SoftText
                    ),
                    navigationIcon = {
                        PastelIconButton(
                            icon = { Icon(Icons.Default.ArrowBack, "Back", tint = SoftText) },
                            onClick = onBack,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    },
                    actions = {
                        val isInputValid = title.isNotBlank()
                        PastelIconButton(
                            icon = { Icon(Icons.Default.Check, "Save", tint = if (isInputValid) LiquidBlue else SoftText.copy(alpha = 0.2f)) },
                            onClick = {
                                if (isInputValid) {
                                    viewModel.addSet(title, description)
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
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(Modifier.height(20.dp))

                PastelGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        TextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = { Text("Tên bộ thẻ học", color = SoftText.copy(alpha = 0.4f)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = SoftText,
                                unfocusedTextColor = SoftText,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                        
                        HorizontalDivider(color = GlassBorder)

                        TextField(
                            value = description,
                            onValueChange = { description = it },
                            placeholder = { Text("Mô tả (không bắt buộc)", color = SoftText.copy(alpha = 0.4f)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = SoftText,
                                unfocusedTextColor = SoftText,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
