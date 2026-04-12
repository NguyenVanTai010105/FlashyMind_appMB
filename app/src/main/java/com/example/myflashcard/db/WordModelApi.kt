package com.example.flashcardapp.db

import com.example.flashcardapp.modal.FolderModal
import com.example.flashcardapp.modal.WordModel
import com.example.flashcardapp.modal.LoginRequest
import com.example.flashcardapp.modal.RegisterRequest
import com.example.flashcardapp.modal.AuthResponse


import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface WordModelApi {
    @GET("api/sets")
    suspend fun getSetsFromServer(@Header("Authorization") token: String): List<FolderModal>

    @POST("api/sets/sync")
    suspend fun syncSet(@Header("Authorization") token: String, @Body set: FolderModal): Response<Any>

    @DELETE("api/sets/{title}")
    suspend fun deleteSetOnServer(@Header("Authorization") token: String, @Path("title") title: String): Response<Any>

    @GET("api/flashcards")
    suspend fun getCardsFromServer(@Header("Authorization") token: String): List<WordModel>

    @POST("api/flashcards/sync")
    suspend fun syncCardProgress(@Header("Authorization") token: String, @Body card: WordModel): Response<Any>

    @DELETE("api/flashcards/{word}")
    suspend fun deleteCardOnServer(@Header("Authorization") token: String, @Path("word") word: String): Response<Any>

    // AUTH
    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Any>

    // AI CHAT
    @POST("api/chat")
    suspend fun sendChat(@Header("Authorization") token: String, @Body request: Map<String, String>): Response<Map<String, Any>>

    @GET("api/chat/history")
    suspend fun getChatHistory(@Header("Authorization") token: String): Response<Map<String, Any>>

    // STATS & GAMIFICATION
    @POST("api/study-session")
    suspend fun saveStudySession(@Header("Authorization") token: String, @Body request: Map<String, Int>): Response<Map<String, Any>>

    @GET("api/stats/dashboard")
    suspend fun getDashboardStats(@Header("Authorization") token: String): Response<Map<String, Any>>
}

object RetrofitInstance {
    private const val BASE_URL = "http://192.168.1.3:8000/"

    val api: WordModelApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WordModelApi::class.java)
    }
}