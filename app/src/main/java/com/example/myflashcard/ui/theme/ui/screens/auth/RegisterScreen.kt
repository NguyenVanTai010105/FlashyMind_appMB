package com.example.flashcardapp.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcardapp.ui.theme.*
import com.example.flashcardapp.ui.components.*
import com.example.flashcardapp.ui.WordModelViewModel
import com.example.flashcardapp.ui.AuthUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: WordModelViewModel,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val authState by viewModel.authState.collectAsState()

    val passwordMismatch = confirmPassword.isNotBlank() && password != confirmPassword

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Tham gia FlashyMind", 
            style = Typography.displayLarge, 
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Tạo tài khoản để mở khóa tiềm năng của bạn", 
            style = Typography.bodyLarge, 
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(48.dp))

        // --- FORM ---
        PastelGlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Địa chỉ Email", color = SoftText.copy(alpha = 0.4f)) },
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = PastelBlue) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = SoftText,
                        unfocusedTextColor = SoftText
                    )
                )

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Mật khẩu", color = SoftText.copy(alpha = 0.4f)) },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = PastelBlue) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                null, tint = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = SoftText,
                        unfocusedTextColor = SoftText
                    )
                )

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)

                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = { Text("Xác nhận mật khẩu", color = Color.White.copy(alpha = 0.4f)) },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = if (passwordMismatch) SoftPink else PastelBlue) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    isError = passwordMismatch,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = SoftText,
                        unfocusedTextColor = SoftText,
                        errorContainerColor = Color.Transparent
                    )
                )
            }
        }

        if (passwordMismatch) {
            Text("Mật khẩu không khớp!", color = SoftPink, style = Typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
        }

        if (authState is AuthUiState.Error) {
            Text(
                text = (authState as AuthUiState.Error).message,
                color = SoftPink, 
                style = Typography.bodyMedium, 
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Spacer(Modifier.height(48.dp))

        // --- ACTION ---
        PastelButton(
            text = "ĐĂNG KÝ",
            onClick = { viewModel.register(email.trim(), password) },
            containerColor = PastelBlue
        )

        Spacer(Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onNavigateToLogin() }
        ) {
            Text("Đã có tài khoản? ", style = Typography.bodyMedium, color = Color.White.copy(alpha = 0.6f))
            Text("Đăng nhập", style = Typography.bodyMedium, color = PastelBlue, fontWeight = FontWeight.Bold)
        }
    }
}
