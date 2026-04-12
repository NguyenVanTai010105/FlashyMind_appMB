package com.example.flashcard.domain

import android.util.Log
import com.example.flashcardapp.db.WordModelApi
import com.example.flashcardapp.di.SessionManager
import com.example.flashcardapp.modal.LoginRequest
import com.example.flashcardapp.modal.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: WordModelApi,
    private val session: SessionManager
) {
    suspend fun login(email: String, password: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.login(LoginRequest(email, password))
                if (response.token != null) {
                    session.saveSession(response.token, response.email ?: email)
                    Result.success(response.message)
                } else {
                    Result.failure(Exception(response.message))
                }
            } catch (e: Exception) {
                Log.e("AUTH", "Login error: ${e.message}")
                Result.failure(Exception("Lỗi: ${e.localizedMessage ?: "Không thể kết nối Server"}"))
            }
        }

    suspend fun register(email: String, password: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.register(RegisterRequest(email, password))
                if (response.token != null) {
                    session.saveSession(response.token, response.email ?: email)
                    Result.success(response.message)
                } else {
                    Result.failure(Exception(response.message))
                }
            } catch (e: Exception) {
                Log.e("AUTH", "Register error: ${e.message}")
                Result.failure(Exception("Lỗi: ${e.localizedMessage ?: "Không thể kết nối Server"}"))
            }
        }

    suspend fun logout() {
        withContext(Dispatchers.IO) {
            try {
                val token = session.getToken()
                token?.let { api.logout("Bearer $it") }
            } catch (e: Exception) {
                Log.e("AUTH", "Logout error: ${e.message}")
            } finally {
                session.clearSession()
            }
        }
    }

    fun isLoggedIn() = session.isLoggedIn()
    fun getEmail() = session.getEmail()
}


