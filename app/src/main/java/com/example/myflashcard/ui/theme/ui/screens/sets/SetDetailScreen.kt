package com.example.flashcardapp.ui.screens.sets

import androidx.compose.animation.*
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LibraryAddCheck
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import com.example.flashcardapp.modal.WordModel
import com.example.flashcardapp.ui.WordModelViewModel
import com.example.flashcardapp.ui.theme.*
import com.example.flashcardapp.ui.components.*
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetDetailScreen(
    setTitle: String,
    viewModel: WordModelViewModel,
    onBack: () -> Unit,
    onNavigateToAddCard: () -> Unit,
    onNavigateToStudy: (List<WordModel>) -> Unit
) {
    val cardsInSetFlow = remember(setTitle) { viewModel.getCardsForSet(setTitle) }
    val cardsInSet by cardsInSetFlow.collectAsState(initial = emptyList())

    // --- STATES ---
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    var isSelectMode by remember { mutableStateOf(false) }
    val selectedCards = remember { mutableStateListOf<WordModel>() }
    
    var showEditCardDialog by remember { mutableStateOf<WordModel?>(null) }
    var cardToDelete by remember { mutableStateOf<WordModel?>(null) }
    var cardsToDeleteBatch by remember { mutableStateOf<List<WordModel>?>(null) }

    val filteredCards = if (searchQuery.isBlank()) {
        cardsInSet
    } else {
        cardsInSet.filter { 
            it.word.contains(searchQuery, ignoreCase = true) || 
            it.meaning.contains(searchQuery, ignoreCase = true) 
        }
    }

    GlobalGlassBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                if (isSelectMode) {
                    // SELECTION TOP BAR
                    CenterAlignedTopAppBar(
                        title = { Text("${selectedCards.size} đã chọn", style = Typography.displaySmall) },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                        navigationIcon = {
                            PastelIconButton(
                                icon = { Icon(Icons.Default.Close, "Cancel", tint = SoftText) },
                                onClick = { 
                                    isSelectMode = false
                                    selectedCards.clear()
                                }
                            )
                        },
                        actions = {
                            PastelIconButton(
                                icon = { Icon(Icons.Default.LibraryAddCheck, "Select All", tint = LiquidBlue) },
                                onClick = { 
                                    if (selectedCards.size == cardsInSet.size) selectedCards.clear()
                                    else {
                                        selectedCards.clear()
                                        selectedCards.addAll(cardsInSet)
                                    }
                                }
                            )
                        }
                    )
                } else {
                    // NORMAL TOP BAR
                    CenterAlignedTopAppBar(
                        title = { Text(setTitle, style = Typography.displaySmall) },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                        navigationIcon = {
                            PastelIconButton(
                                icon = { Icon(Icons.Default.ArrowBack, "Back", tint = SoftText) },
                                onClick = onBack,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        },
                        actions = {
                            PastelIconButton(
                                icon = { Icon(Icons.Default.Search, "Search", tint = SoftText) },
                                onClick = { isSearchVisible = !isSearchVisible },
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                    )
                }
            },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .height(70.dp)
                            .fillMaxWidth()
                            .shadow(20.dp, RoundedCornerShape(35.dp)),
                        color = GlassWhiteHeavy,
                        shape = RoundedCornerShape(35.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                    ) {
                        if (isSelectMode) {
                            // SELECTION ACTIONS
                            Row(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { 
                                    if (selectedCards.isNotEmpty()) cardsToDeleteBatch = selectedCards.toList()
                                }) {
                                    Icon(Icons.Default.Delete, "Delete Selected", tint = PastelRed, modifier = Modifier.size(28.dp))
                                }
                                
                                Button(
                                    onClick = { 
                                        isSelectMode = false
                                        selectedCards.clear()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = LiquidBlue.copy(alpha = 0.2f)),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text("Xong", color = LiquidBlue, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            // NORMAL ACTIONS
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // PLAY
                                IconButton(onClick = { onNavigateToStudy(cardsInSet) }) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.PlayArrow, null, tint = LiquidBlue, modifier = Modifier.size(28.dp))
                                        Text("Học", style = Typography.labelSmall, color = LiquidBlue)
                                    }
                                }
                                // ADD
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(LiquidGradient)
                                        .clickable { onNavigateToAddCard() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(30.dp))
                                }
                                // SELECT
                                IconButton(onClick = { isSelectMode = true }) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Checklist, null, tint = SoftText.copy(alpha = 0.6f), modifier = Modifier.size(28.dp))
                                        Text("Chọn", style = Typography.labelSmall, color = SoftText.copy(alpha = 0.6f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                // SEARCH BAR TOGGLE
                AnimatedVisibility(
                    visible = isSearchVisible,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    PastelGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalPadding = 12.dp,
                        verticalPadding = 8.dp,
                        cornerRadius = 20.dp,
                        backgroundColor = GlassWhiteLight
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Tìm kiếm trong bộ thẻ...", color = SoftText.copy(alpha = 0.4f)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = SoftText
                            ),
                            trailingIcon = {
                                IconButton(onClick = { 
                                    searchQuery = ""
                                    isSearchVisible = false
                                }) { Icon(Icons.Default.Close, null, tint = SoftText.copy(alpha = 0.4f)) }
                            }
                        )
                    }
                }

                // CARD LIST
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(1.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    items(filteredCards) { card ->
                        val isSelected = selectedCards.contains(card)
                        var showMeaning by remember { mutableStateOf(false) }
                        
                        SwipeableItemContainer(
                            onAction = { showEditCardDialog = card },
                            actionColor = LiquidBlue.copy(alpha = 0.5f),
                            enabled = !isSelectMode
                        ) {
                            PastelGlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { 
                                        if (isSelectMode) {
                                            if (isSelected) selectedCards.remove(card) else selectedCards.add(card)
                                        } else {
                                            showMeaning = !showMeaning
                                        }
                                    },
                                horizontalPadding = 12.dp,
                                verticalPadding = 4.dp,
                                cornerRadius = 16.dp,
                                backgroundColor = if (isSelected) SelectedYellowGlass else GlassWhite
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 20.dp, vertical = 20.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (isSelectMode) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .border(2.dp, if (isSelected) SelectedYellow else SoftText.copy(alpha = 0.2f), CircleShape)
                                                    .background(if (isSelected) SelectedYellow else Color.Transparent),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (isSelected) Icon(Icons.Default.Check, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                            }
                                            Spacer(Modifier.width(16.dp))
                                        }

                                        Text(
                                            text = card.word,
                                            style = Typography.displaySmall,
                                            color = SoftText,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    
                                    AnimatedVisibility(
                                        visible = showMeaning && !isSelectMode,
                                        enter = expandVertically(),
                                        exit = shrinkVertically()
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .padding(bottom = 16.dp, start = 20.dp, end = 20.dp)
                                                .fillMaxWidth()
                                        ) {
                                            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                                            Spacer(Modifier.height(12.dp))
                                            
                                            if (!card.imageUri.isNullOrBlank()) {
                                                AsyncImage(
                                                    model = card.imageUri,
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(150.dp)
                                                        .clip(RoundedCornerShape(16.dp))
                                                        .background(Color.White.copy(alpha = 0.05f)),
                                                    contentScale = ContentScale.Fit
                                                )
                                                Spacer(Modifier.height(12.dp))
                                            }

                                            Text(
                                                text = card.meaning,
                                                style = Typography.bodyLarge,
                                                color = SoftText.copy(alpha = 0.8f),
                                                lineHeight = 24.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // --- DIALOGS (Confirm Delete) ---
                if (cardToDelete != null) {
                    AlertDialog(
                        onDismissRequest = { cardToDelete = null },
                        containerColor = DeepGlass,
                        modifier = Modifier.border(1.dp, GlassBorder, RoundedCornerShape(28.dp)),
                        title = { Text("Xóa thẻ?", style = Typography.headlineMedium, color = SoftText) },
                        text = { Text("Bạn có chắc chắn muốn xóa thẻ này?", color = SoftText.copy(alpha = 0.8f)) },
                        confirmButton = {
                            Button(onClick = { viewModel.deleteCard(cardToDelete!!); cardToDelete = null }, colors = ButtonDefaults.buttonColors(containerColor = PastelRed)) {
                                Text("XÓA", color = Color.White)
                            }
                        },
                        dismissButton = { TextButton(onClick = { cardToDelete = null }) { Text("HỦY", color = SoftText) } }
                    )
                }

                if (cardsToDeleteBatch != null) {
                    AlertDialog(
                        onDismissRequest = { cardsToDeleteBatch = null },
                        containerColor = DeepGlass,
                        modifier = Modifier.border(1.dp, GlassBorder, RoundedCornerShape(28.dp)),
                        title = { Text("Xóa ${cardsToDeleteBatch?.size} thẻ?", style = Typography.headlineMedium, color = SoftText) },
                        text = { Text("Hành động này không thể hoàn tác.", color = SoftText.copy(alpha = 0.8f)) },
                        confirmButton = {
                            Button(onClick = { 
                                cardsToDeleteBatch?.forEach { viewModel.deleteCard(it) }
                                isSelectMode = false
                                selectedCards.clear()
                                cardsToDeleteBatch = null 
                            }, colors = ButtonDefaults.buttonColors(containerColor = PastelRed)) {
                                Text("XÓA TẤT CẢ", color = Color.White)
                            }
                        },
                        dismissButton = { TextButton(onClick = { cardsToDeleteBatch = null }) { Text("HỦY", color = SoftText) } }
                    )
                }
            }
        }
    }

    // --- UPGRADED EDIT DIALOG ---
    if (showEditCardDialog != null) {
        val editingCard = showEditCardDialog!!
        var editWord by remember(editingCard) { mutableStateOf(editingCard.word) }
        var editMeaning by remember(editingCard) { mutableStateOf(editingCard.meaning) }
        var editImageUri by remember(editingCard) { mutableStateOf(editingCard.imageUri) }
        val isTranslating by viewModel.isTranslating.collectAsState()

        val imagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri -> editImageUri = uri?.toString() }
        
        AlertDialog(
            onDismissRequest = { showEditCardDialog = null },
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(32.dp))
                .border(1.dp, GlassBorder, RoundedCornerShape(32.dp)),
            containerColor = DeepGlass,
            title = { 
                Text("CẬP NHẬT THẺ", style = Typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White) 
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // WORD INPUT
                    TextField(
                        value = editWord,
                        onValueChange = { editWord = it },
                        label = { Text("Từ vựng", color = SoftText.copy(alpha = 0.7f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.05f),
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = SoftText,
                            unfocusedTextColor = SoftText
                        )
                    )

                    // MEANING INPUT WITH AI
                    TextField(
                        value = editMeaning,
                        onValueChange = { editMeaning = it },
                        label = { Text("Ý nghĩa", color = SoftText.copy(alpha = 0.7f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.05f),
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = SoftText,
                            unfocusedTextColor = SoftText
                        ),
                        trailingIcon = {
                            if (isTranslating) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = PastelBlue, strokeWidth = 2.dp)
                            } else {
                                IconButton(onClick = { 
                                    if (editWord.isNotBlank()) viewModel.translateWord(editWord) { editMeaning = it }
                                }) {
                                    Icon(Icons.Default.AutoAwesome, "Dịch AI", tint = PastelBlue)
                                }
                            }
                        }
                    )

                    // IMAGE PREVIEW / SELECTOR
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (!editImageUri.isNullOrBlank()) {
                            AsyncImage(
                                model = editImageUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                            // "TAP TO CHANGE" LABEL
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(32.dp)
                                    .align(Alignment.BottomCenter)
                                    .background(Color.Black.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("CHẠM ĐỂ THAY ĐỔI", color = Color.White, style = Typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.AddPhotoAlternate, null, tint = SoftText.copy(alpha = 0.5f), modifier = Modifier.size(32.dp))
                                Spacer(Modifier.height(4.dp))
                                Text("Thêm ảnh minh họa", color = SoftText.copy(alpha = 0.5f), style = Typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                PastelButton(
                    text = "CẬP NHẬT",
                    onClick = {
                        viewModel.deleteCard(editingCard) // Delete old one because primary key changed
                        viewModel.addCard(editWord, editMeaning, setTitle, editImageUri)
                        showEditCardDialog = null
                    },
                    containerColor = PastelBlue,
                    modifier = Modifier.width(140.dp)
                )
            },
            dismissButton = {
                TextButton(onClick = { showEditCardDialog = null }) { 
                    Text("HỦY", color = Color.White.copy(alpha = 0.6f)) 
                }
            }
        )
    }
}
