package com.example.flashcardapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcardapp.modal.*
import com.example.flashcardapp.domain.*
import com.example.flashcardapp.data.UserPreferencesRepository
import com.google.mlkit.nl.translate.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WordModelViewModel @Inject constructor(
    private val repository: WordModelRepository,
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    // --- STATE QUẢN LÝ ---
    val isDarkMode = userPreferences.isDarkMode.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    fun toggleDarkMode(isDark: Boolean) {
        viewModelScope.launch { userPreferences.toggleDarkMode(isDark) }
    }

    // Luồng Email người dùng hiện tại
    private val _currentUserEmail = MutableStateFlow(authRepository.getEmail() ?: "")
    val currentUserEmail = _currentUserEmail.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val allSets = _currentUserEmail.flatMapLatest { email ->
        if (email.isBlank()) flowOf(emptyList()) else repository.getAllSets(email)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val allCards = _currentUserEmail.flatMapLatest { email ->
        if (email.isBlank()) flowOf(emptyList()) else repository.getAllCards(email)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val filteredSets = combine(allSets, _searchQuery) { sets, query ->
        if (query.isBlank()) sets else sets.filter { it.title.contains(query, true) || it.description.contains(query, true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- GAMIFICATION STATE ---
    private val _userStats = MutableStateFlow<UserStats?>(null)
    val userStats = _userStats.asStateFlow()

    private var currentSessionCorrect = 0
    private var currentSessionTotal = 0
    private var currentSessionXP = 0

    fun loadUserStats() {
        viewModelScope.launch {
            repository.getDashboardStats().onSuccess { _userStats.value = it }
        }
    }

    fun resetSession() {
        currentSessionCorrect = 0
        currentSessionTotal = 0
        currentSessionXP = 0
    }

    fun finishStudySession() {
        if (currentSessionTotal == 0) return
        viewModelScope.launch {
            repository.saveStudySession(currentSessionTotal, currentSessionCorrect, currentSessionXP)
            loadUserStats()
        }
    }

    val cardsDueToday = combine(allCards, _currentUserEmail) { list, email ->
        if (email.isBlank()) emptyList() else list.filter { it.nextReviewDate <= System.currentTimeMillis() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studiedCards = allCards.map { list -> list.filter { it.repetition > 0 } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) { _searchQuery.value = query }
    fun getCardsForSet(setTitle: String) = _currentUserEmail.flatMapLatest { email ->
        repository.getCardsBySet(setTitle, email)
    }

    // --- HÀM NGHIỆP VỤ ---
    fun addSet(title: String, desc: String = "") {
        if (title.isNotBlank()) viewModelScope.launch { 
            repository.addSet(FolderModal(title = title, description = desc, userEmail = _currentUserEmail.value)) 
        }
    }
    fun deleteSet(set: FolderModal) = viewModelScope.launch { repository.deleteSet(set) }
    fun updateSetDescription(title: String, desc: String) = viewModelScope.launch { 
        repository.updateSet(FolderModal(title = title, description = desc, userEmail = _currentUserEmail.value)) 
    }

    fun addCard(word: String, meaning: String, setTitle: String, uri: String? = null) {
        if (word.isNotBlank() && meaning.isNotBlank() && setTitle.isNotBlank())
            viewModelScope.launch { 
                repository.addCard(WordModel(word = word, setTitle = setTitle, meaning = meaning, imageUri = uri, userEmail = _currentUserEmail.value)) 
            }
    }
    fun deleteCard(card: WordModel) = viewModelScope.launch { repository.deleteCard(card) }
    fun editCardMeaning(card: WordModel, meaning: String, uri: String? = card.imageUri) {
        if (meaning.isNotBlank()) viewModelScope.launch { 
            repository.updateCard(card.copy(meaning = meaning, imageUri = uri)) 
        }
    }
    fun updateCardStats(card: WordModel, quality: Int) {
        viewModelScope.launch {
            repository.updateCardStats(card, quality)
            
            currentSessionTotal++
            if (quality >= 3) {
                currentSessionCorrect++
                currentSessionXP += 10
            } else {
                currentSessionXP += 2
            }
        }
    }
    fun syncDataFromServer() = viewModelScope.launch { repository.pullFromServer() }

    // --- AUTHENTICATION ---
    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authState = _authState.asStateFlow()
    val isLoggedIn get() = authRepository.isLoggedIn()
    val loggedInEmail get() = authRepository.getEmail()

    private fun handleAuth(call: suspend () -> Result<String>) = viewModelScope.launch {
        _authState.value = AuthUiState.Loading
        val result = call()
        _authState.value = result.fold(
            onSuccess = { 
                _currentUserEmail.value = authRepository.getEmail() ?: ""
                syncDataFromServer() // Tự động đồng bộ ngay sau khi login
                AuthUiState.Success 
            },
            onFailure = { AuthUiState.Error(it.message ?: "Lỗi") }
        )
    }

    fun login(e: String, p: String) = handleAuth { authRepository.login(e, p) }
    fun register(e: String, p: String) = handleAuth { authRepository.register(e, p) }
    fun logout(onDone: () -> Unit) = viewModelScope.launch { 
        // KHÔNG xóa sạch local data ở đây để tránh mất dữ liệu chưa sync
        authRepository.logout()
        _currentUserEmail.value = ""
        _authState.value = AuthUiState.Idle
        onDone() 
    }
    fun resetAuthState() { _authState.value = AuthUiState.Idle }

    // --- AI CHAT MANAGEMENT ---
    val chatMessages = repository.getChatMessages().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private val _isAITyping = MutableStateFlow(false)
    val isAITyping = _isAITyping.asStateFlow()

    fun sendChatMessage(msg: String) {
        if (msg.isBlank()) return
        viewModelScope.launch {
            _isAITyping.value = true
            repository.sendChatToAi(msg)
            _isAITyping.value = false
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch { repository.clearChatHistory() }
    }

    // --- AI TRANSLATION ---
    private val _isTranslating = MutableStateFlow(false)
    val isTranslating = _isTranslating.asStateFlow()

    fun translateWord(word: String, onResult: (String) -> Unit) {
        if (word.isBlank()) return
        _isTranslating.value = true
        val options = TranslatorOptions.Builder().setSourceLanguage(TranslateLanguage.ENGLISH).setTargetLanguage(TranslateLanguage.VIETNAMESE).build()
        val translator = Translation.getClient(options)

        translator.downloadModelIfNeeded().addOnSuccessListener {
            translator.translate(word).addOnSuccessListener { 
                _isTranslating.value = false; onResult(it); translator.close() 
            }.addOnFailureListener { _isTranslating.value = false; onResult("Lỗi: ${it.message}") }
        }.addOnFailureListener { _isTranslating.value = false }
    }
}