package com.example.flashcardapp.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import com.example.flashcardapp.db.DBHandler
import com.example.flashcardapp.db.WordModelApi
import com.example.flashcardapp.di.SessionManager

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val dao: DBHandler,
    private val api: WordModelApi,
    private val session: SessionManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val email = session.getEmail() ?: return Result.failure()
        val token = "Bearer ${session.getToken() ?: ""}"
        return try {
            val unsyncedSets = dao.getUnsyncedSets(email)
            for (set in unsyncedSets) {
                val response = api.syncSet(token, set)
                if (response.isSuccessful) {
                    dao.insertSet(set.copy(isSynced = true))
                }
            }

            val unsyncedCards = dao.getUnsyncedCards(email)
            for (card in unsyncedCards) {
                val response = api.syncCardProgress(token, card)
                if (response.isSuccessful) {
                    dao.updateCard(card.copy(isSynced = true))
                }
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error syncing data: ${e.message}")
            Result.retry()
        }
    }
}
