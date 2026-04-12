package com.example.flashcardapp.domain

import com.example.flashcardapp.modal.WordModel
import com.example.flashcardapp.modal.FolderModal
import com.example.flashcardapp.modal.ChatMessage
import com.example.flashcardapp.modal.UserStats
import kotlinx.coroutines.flow.Flow

interface WordModelRepository {
    // ---- QUẢN LÝ BỘ THẺ (SETS) ----
    fun getAllSets(email: String): Flow<List<FolderModal>>
    suspend fun addSet(set: FolderModal)
    suspend fun updateSet(set: FolderModal)
    suspend fun deleteSet(set: FolderModal)

    // ---- QUẢN LÝ TỪ VỰNG (CARDS) ----
    fun getAllCards(email: String): Flow<List<WordModel>>
    fun getCardsBySet(setTitle: String, email: String): Flow<List<WordModel>>
    fun getCardsToReview(currentTime: Long, email: String): Flow<List<WordModel>>

    suspend fun addCard(card: WordModel)
    suspend fun updateCard(card: WordModel)
    suspend fun deleteCard(card: WordModel)
    suspend fun updateCardStats(card: WordModel, quality: Int)

    // ---- AI CHAT ----
    fun getChatMessages(): Flow<List<ChatMessage>>
    suspend fun sendChatToAi(message: String)
    suspend fun clearChatHistory()
    suspend fun clearAllLocalData()

    // ---- THỐNG KÊ & GAMIFICATION ----
    suspend fun getDashboardStats(): kotlin.Result<UserStats>
    suspend fun saveStudySession(cardsStudied: Int, correctCount: Int, xpGained: Int): kotlin.Result<Unit>

    suspend fun pullFromServer()
}