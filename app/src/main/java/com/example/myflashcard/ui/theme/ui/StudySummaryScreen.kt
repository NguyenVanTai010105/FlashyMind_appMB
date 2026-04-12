package com.example.flashcardapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StudySummaryScreen(
    knownCount: Int,
    learningCount: Int,
    onClose: () -> Unit,
    onReviewQuestions: () -> Unit,
    onContinueReview: () -> Unit,
    onResetWordModels: () -> Unit
) {
    val totalCards = knownCount + learningCount
    val progressPercentage = if (totalCards == 0) 0 else (knownCount * 100) / totalCards
    val progressFloat = progressPercentage / 100f

    val bgColor = Color(0xFF0B0D28)
    val orangeColor = Color(0xFFFF9933)
    val greenColor = Color(0xFF5BD092)
    val primaryBtnColor = Color(0xFF5A69FF)
    val secondaryBtnColor = Color(0xFF383C55)

    Column(
        modifier = Modifier.fillMaxSize().background(bgColor).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onClose, modifier = Modifier.background(secondaryBtnColor, CircleShape)) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
            Text(text = "$totalCards/$totalCards", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = { /* Mở Cài đặt */ }, modifier = Modifier.background(secondaryBtnColor, CircleShape)) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Header
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Bạn thật cừ! Bây giờ hãy\nthử một số câu hỏi mẫu\ntrong chế độ Học.",
                color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f)
            )
            Box(modifier = Modifier.size(80.dp).background(orangeColor.copy(alpha = 0.2f), RoundedCornerShape(10.dp)))
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Tiến độ
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Tiến độ của bạn", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                // Vòng tròn
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(progress = { 1f }, color = orangeColor.copy(alpha = 0.2f), strokeWidth = 12.dp, modifier = Modifier.size(100.dp))
                    CircularProgressIndicator(progress = { progressFloat }, color = orangeColor, strokeWidth = 12.dp, modifier = Modifier.size(100.dp))
                    Text(text = "$progressPercentage%", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(20.dp))
                // Thanh thống kê
                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth().background(greenColor, RoundedCornerShape(50)).padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Đã biết", color = bgColor, fontWeight = FontWeight.Bold)
                        Text("$knownCount", color = bgColor, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth().background(orangeColor, RoundedCornerShape(50)).padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Đang học", color = bgColor, fontWeight = FontWeight.Bold)
                        Text("$learningCount", color = bgColor, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Các nút bấm
        TextButton(onClick = { /* TODO */ }) { Text("Quay lại câu hỏi cuối cùng", color = Color(0xFFAAB0CC), fontWeight = FontWeight.Bold) }
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = onReviewQuestions, modifier = Modifier.fillMaxWidth().height(55.dp), colors = ButtonDefaults.buttonColors(containerColor = primaryBtnColor), shape = RoundedCornerShape(20.dp)) {
            Text("Ôn luyện với các câu hỏi", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = onContinueReview, modifier = Modifier.fillMaxWidth().height(55.dp), colors = ButtonDefaults.buttonColors(containerColor = secondaryBtnColor), shape = RoundedCornerShape(20.dp)) {
            Text("Tiếp tục ôn 1 thuật ngữ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(modifier = Modifier.height(10.dp))
        TextButton(onClick = onResetWordModels) { Text("Đặt lại Thẻ ghi nhớ", color = Color(0xFFAAB0CC), fontWeight = FontWeight.Bold) }
        Spacer(modifier = Modifier.height(20.dp))
    }
}