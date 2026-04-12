package com.example.flashcardapp.ui.navigation

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.flashcardapp.modal.WordModel
import com.example.flashcardapp.ui.AuthUiState
import com.example.flashcardapp.ui.WordModelViewModel
import com.example.flashcardapp.ui.screens.auth.*
import com.example.flashcardapp.ui.screens.cards.*
import com.example.flashcardapp.ui.screens.main.*
import com.example.flashcardapp.ui.screens.sets.*
import com.example.flashcardapp.ui.screens.study.*
import com.example.flashcardapp.ui.screens.profile.*
import com.example.flashcardapp.ui.screens.ai.*
import com.example.flashcardapp.ui.theme.*
import com.example.flashcardapp.ui.components.*

@Composable
fun AppNavigation(viewModel: WordModelViewModel, tts: TextToSpeech?) {
    var currentScreen by remember {
        mutableStateOf(if (viewModel.isLoggedIn) "DASHBOARD" else "LOGIN")
    }
    var selectedSetTitle by remember { mutableStateOf("") }
    var studyList by remember { mutableStateOf<List<WordModel>?>(null) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthUiState.Success) {
            currentScreen = "DASHBOARD"
            viewModel.resetAuthState()
        }
    }

    val mainTabs = listOf("DASHBOARD", "HOME", "AI_CHAT", "PROFILE")
    val isMainTab = currentScreen == "DASHBOARD"

    GlobalGlassBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                if (isMainTab) {
                    val tabs = listOf(
                        TabItem("DASHBOARD", "Home", Icons.Default.Home),
                        TabItem("HOME", "Decks", Icons.Default.CollectionsBookmark),
                        TabItem("AI_CHAT", "Chat", Icons.Default.ChatBubble),
                        TabItem("PROFILE", "User", Icons.Default.Person)
                    )
                    
                    FloatingPillNav(
                        tabs = tabs,
                        currentTab = currentScreen,
                        onTabSelected = { 
                            currentScreen = it
                        }
                    )
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isMainTab) padding else PaddingValues())
            ) {
                when (currentScreen) {
                    "LOGIN" -> LoginScreen(
                        viewModel = viewModel,
                        onNavigateToRegister = { currentScreen = "REGISTER" }
                    )
                    "REGISTER" -> RegisterScreen(
                        viewModel = viewModel,
                        onNavigateToLogin = { currentScreen = "LOGIN" }
                    )
                    "DASHBOARD" -> DashboardScreen(
                        viewModel = viewModel,
                        onStudyDue = { list ->
                            studyList = list
                            currentScreen = "STUDY"
                        },
                        onNavigateToAddSet = { currentScreen = "ADD_SET" },
                        onSetSelected = { setTitle ->
                            selectedSetTitle = setTitle
                            currentScreen = "SET_DETAIL"
                        }
                    )
                    "HOME" -> DecksScreen(
                        viewModel = viewModel,
                        onNavigateToAddSet = { currentScreen = "ADD_SET" },
                        onSetSelected = { setTitle ->
                            selectedSetTitle = setTitle
                            currentScreen = "SET_DETAIL"
                        },
                        onBack = { currentScreen = "DASHBOARD" }
                    )
                    "AI_CHAT" -> AiChatScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = "DASHBOARD" }
                    )
                    "PROFILE" -> ProfileScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = "DASHBOARD" },
                        onLogout = {
                            viewModel.logout { currentScreen = "LOGIN" }
                        }
                    )
                    "ADD_SET" -> AddSetScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = "HOME" }
                    )
                    "SET_DETAIL" -> SetDetailScreen(
                        setTitle = selectedSetTitle,
                        viewModel = viewModel,
                        onBack = { currentScreen = "HOME" },
                        onNavigateToAddCard = { currentScreen = "ADD_CARD" },
                        onNavigateToStudy = { list ->
                            studyList = list
                            currentScreen = "STUDY"
                        }
                    )
                    "ADD_CARD" -> AddCardScreen(
                        setTitle = selectedSetTitle,
                        viewModel = viewModel,
                        onBack = { currentScreen = "SET_DETAIL" }
                    )
                    "STUDY" -> StudyScreen(
                        studyList = studyList ?: emptyList(),
                        viewModel = viewModel,
                        tts = tts,
                        onBack = { currentScreen = "SET_DETAIL" }
                    )
                }
            }
        }
    }
}
