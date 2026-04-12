package com.example.flashcardapp.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.flashcardapp.modal.FolderModal
import com.example.flashcardapp.modal.WordModel

@Dao
interface DBHandler {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: FolderModal)

    @Update
    suspend fun updateSet(set: FolderModal)

    @Delete
    suspend fun deleteSet(set: FolderModal)

    @Query("DELETE FROM flashcard_sets WHERE userEmail = :email")
    suspend fun clearSetsForUser(email: String)

    @Query("SELECT * FROM flashcard_sets WHERE userEmail = :email")
    fun getAllSetsForUser(email: String): Flow<List<FolderModal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: WordModel)

    @Update
    suspend fun updateCard(card: WordModel)

    @Delete
    suspend fun deleteCard(card: WordModel)

    @Query("DELETE FROM flashcards WHERE userEmail = :email")
    suspend fun clearCardsForUser(email: String)

    @Query("SELECT * FROM flashcards WHERE userEmail = :email")
    fun getAllCardsForUser(email: String): Flow<List<WordModel>>

    @Query("SELECT * FROM flashcards WHERE setTitle = :setTitle AND userEmail = :email")
    fun getCardsBySet(setTitle: String, email: String): Flow<List<WordModel>>

    @Query("SELECT * FROM flashcards WHERE nextReviewDate <= :currentTime AND userEmail = :email")
    fun getCardsDue(currentTime: Long, email: String): Flow<List<WordModel>>

    @Query("SELECT * FROM flashcard_sets WHERE isSynced = 0 AND userEmail = :email")
    suspend fun getUnsyncedSets(email: String): List<FolderModal>

    @Query("SELECT * FROM flashcards WHERE isSynced = 0 AND userEmail = :email")
    suspend fun getUnsyncedCards(email: String): List<WordModel>
}