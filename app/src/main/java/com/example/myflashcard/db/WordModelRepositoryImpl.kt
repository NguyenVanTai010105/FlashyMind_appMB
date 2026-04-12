package com.example.flashcardapp.db

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.flashcardapp.di.SessionManager
import com.example.flashcardapp.domain.WordModelRepository
import com.example.flashcardapp.modal.*
import com.example.flashcardapp.worker.SyncWorker
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class WordModelRepositoryImpl(
    private val dao: DBHandler,
    private val chatDao: ChatDao,
    private val api: WordModelApi,
    private val session: SessionManager,
    private val context: Context
) : WordModelRepository {

    override fun getChatMessages() = chatDao.getAllMessages()

    override suspend fun clearChatHistory() = chatDao.clearHistory()

    override suspend fun clearAllLocalData() {
        val email = session.getEmail()
        if (email != null) {
            withContext(Dispatchers.IO) {
                dao.clearSetsForUser(email)
                dao.clearCardsForUser(email)
                chatDao.clearHistory()
            }
        }
    }

    override suspend fun sendChatToAi(message: String) {
        val userMsg = ChatMessage(role = "user", message = message)
        chatDao.insertMessage(userMsg)

        try {
            val token = "Bearer ${session.getToken() ?: ""}"
            val response = api.sendChat(token, mapOf("message" to message))
            
            if (response.isSuccessful) {
                val aiResponse = response.body()?.get("message") as? String ?: "AI không phản hồi."
                chatDao.insertMessage(ChatMessage(role = "assistant", message = aiResponse))
            } else {
                val errorMsg = when (response.code()) {
                    429 -> "Flashy hết lượt dùng hôm nay rồi! Hãy quay lại vào ngày mai nhé. 🌟"
                    503 -> "Server đang bảo trì hoặc chưa bật. Vui lòng kiểm tra kết nối! 🛠️"
                    else -> "Lỗi kết nối Server: ${response.code()}"
                }
                chatDao.insertMessage(ChatMessage(role = "assistant", message = errorMsg))
            }
        } catch (e: Exception) {
            chatDao.insertMessage(ChatMessage(role = "assistant", message = "Không thể kết nối Server. Vui lòng kiểm tra Internet! 🌐"))
        }
    }

    private fun getAuthToken() = "Bearer ${session.getToken() ?: ""}"

    private fun enqueueSync() {
        val request = OneTimeWorkRequestBuilder<SyncWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork("SyncWorker", ExistingWorkPolicy.REPLACE, request)
    }

    private suspend fun <T> performSync(apiCall: suspend (String) -> Response<T>, onSuccess: suspend (T?) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                val response = apiCall(getAuthToken())
                if (response.isSuccessful) onSuccess(response.body()) else enqueueSync()
            } catch (e: Exception) {
                Log.e("SYNC_API", "Sync error: ${e.message}")
                enqueueSync()
            }
        }
    }

    override fun getAllSets(email: String) = dao.getAllSetsForUser(email)
    override fun getAllCards(email: String) = dao.getAllCardsForUser(email)
    override fun getCardsBySet(setTitle: String, email: String) = dao.getCardsBySet(setTitle, email)
    override fun getCardsToReview(time: Long, email: String) = dao.getCardsDue(time, email)

    override suspend fun getDashboardStats(): kotlin.Result<UserStats> {
        return withContext(Dispatchers.IO) {
            try {
                val token = "Bearer ${session.getToken() ?: ""}"
                val response = api.getDashboardStats(token)
                if (response.isSuccessful) {
                    val body = response.body() ?: return@withContext kotlin.Result.failure(Exception("Empty response"))
                    val userData = body["user"] as? Map<*, *> ?: return@withContext kotlin.Result.failure(Exception("Invalid user data"))
                    
                    // Ép kiểu an toàn (Safe casting) cho cả Double và Int từ JSON
                    fun toInt(value: Any?): Int = when(value) {
                        is Double -> value.toInt()
                        is Int -> value
                        is String -> value.toIntOrNull() ?: 0
                        else -> 0
                    }

                    val stats = UserStats(
                        xp = toInt(userData["xp"]),
                        streak = toInt(userData["streak"]),
                        level = toInt(userData["level"]).coerceAtLeast(1)
                    )
                    kotlin.Result.success(stats)
                } else {
                    kotlin.Result.failure(Exception("API Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Log.e("REPO_STATS", "Error getting stats: ${e.message}")
                kotlin.Result.failure(e)
            }
        }
    }

    override suspend fun saveStudySession(cardsStudied: Int, correctCount: Int, xpGained: Int): kotlin.Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val token = "Bearer ${session.getToken() ?: ""}"
                val request = mapOf(
                    "cards_studied" to cardsStudied,
                    "correct_count" to correctCount,
                    "xp_gained" to xpGained
                )
                val response = api.saveStudySession(token, request)
                if (response.isSuccessful) kotlin.Result.success(Unit) else kotlin.Result.failure(Exception("API Error"))
            } catch (e: Exception) {
                kotlin.Result.failure(e)
            }
        }
    }

    override suspend fun addSet(set: FolderModal) {
        val email = session.getEmail() ?: return
        val newSet = set.copy(userEmail = email, isSynced = false)
        dao.insertSet(newSet)
        performSync({ api.syncSet(it, newSet) }) { dao.insertSet(newSet.copy(isSynced = true)) }
    }

    override suspend fun updateSet(set: FolderModal) {
        val email = session.getEmail() ?: return
        val updatedSet = set.copy(userEmail = email, isSynced = false)
        dao.updateSet(updatedSet)
        performSync({ api.syncSet(it, updatedSet) }) { dao.updateSet(updatedSet.copy(isSynced = true)) }
    }

    override suspend fun deleteSet(set: FolderModal) {
        dao.deleteSet(set)
        performSync({ api.deleteSetOnServer(it, set.title) }) {}
    }

    override suspend fun addCard(card: WordModel) {
        val email = session.getEmail() ?: return
        val newCard = card.copy(userEmail = email, isSynced = false)
        dao.insertCard(newCard)
        performSync({ api.syncCardProgress(it, newCard) }) { dao.updateCard(newCard.copy(isSynced = true)) }
    }

    override suspend fun updateCard(card: WordModel) {
        val email = session.getEmail() ?: return
        dao.updateCard(card.copy(userEmail = email, isSynced = false))
        performSync({ api.syncCardProgress(it, card) }) { dao.updateCard(card.copy(isSynced = true)) }
    }

    override suspend fun updateCardStats(card: WordModel, quality: Int) {
        val email = session.getEmail() ?: return
        val q = quality.coerceIn(0, 5)
        
        var n = card.repetition
        var i = card.interval
        var ef = card.easeFactor
        
        if (q >= 3) {
            when (n) {
                0 -> i = 1
                1 -> i = 6
                else -> i = (i * ef).toInt().coerceAtLeast(1)
            }
            n += 1
        } else {
            n = 0
            i = 1
        }
        
        ef = ef + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
        if (ef < 1.3) ef = 1.3
        
        val nextDate = System.currentTimeMillis() + (i * 24 * 60 * 60 * 1000L)
        
        val updatedCard = card.copy(
            userEmail = email,
            repetition = n,
            interval = i,
            easeFactor = ef,
            nextReviewDate = nextDate,
            isSynced = false
        )
        
        dao.updateCard(updatedCard)
        performSync({ api.syncCardProgress(it, updatedCard) }) { dao.updateCard(updatedCard.copy(isSynced = true)) }
    }

    override suspend fun deleteCard(card: WordModel) {
        dao.deleteCard(card)
        performSync({ api.deleteCardOnServer(it, card.word) }) {}
    }

    override suspend fun pullFromServer() {
        val email = session.getEmail() ?: return
        withContext(Dispatchers.IO) {
            try {
                val token = getAuthToken()
                val sets = api.getSetsFromServer(token)
                val cards = api.getCardsFromServer(token)
                
                // Thay vì xóa tất cả, ta chèn mới hoặc cập nhật (Upsert)
                // Dữ liệu từ server luôn được coi là 'isSynced = true'
                sets.forEach { dao.insertSet(it.copy(userEmail = email, isSynced = true)) }
                cards.forEach { dao.insertCard(it.copy(userEmail = email, isSynced = true)) }
            } catch (e: Exception) { 
                Log.e("SYNC_API", "Pull error: ${e.message}") 
            }
        }
    }
}