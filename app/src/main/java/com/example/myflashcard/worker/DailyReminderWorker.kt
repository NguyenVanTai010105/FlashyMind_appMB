package com.example.flashcardapp.worker

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.flashcardapp.MainActivity
import com.example.flashcardapp.db.AppDatabase
import com.example.flashcardapp.di.SessionManager
import kotlinx.coroutines.flow.first

class DailyReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val sessionManager = SessionManager(context)
            val email = sessionManager.getEmail() ?: return Result.success() // Không đăng nhập thì không nhắc

            val database = AppDatabase.getDatabase(context)
            val dao = database.flashcardDao()
            val currentTime = System.currentTimeMillis()

            val cardsDue = dao.getCardsDue(currentTime, email).first()

            // Chỉ gửi thông báo khi thực sự có thẻ cần ôn tập
            if (cardsDue.isNotEmpty()) {
                showNotification(cardsDue.size)
            } else {
                Log.d("DailyReminderWorker", "Không có thẻ nào cần ôn tập hôm nay, bỏ qua thông báo.")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("DailyReminderWorker", "Lỗi truy xuất DB khi nhắc nhở: ${e.message}")
            Result.retry()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(cardCount: Int) {
        val channelId = "flashcard_reminder_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Nhắc nhở học tập",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Thông báo nhắc ôn tập WordModel hàng ngày"
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("Học bài thôi bạn ơi! \uD83D\uDCDA")
            .setContentText("Hôm nay bạn có $cardCount thẻ đang chờ được ôn lại.")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Hôm nay bạn có $cardCount thẻ đang chờ được ôn lại. Tranh thủ vài phút vào app ngay để không phá vỡ chuỗi học tập nhé!"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // --- CÁCH VIẾT MỚI: KIỂM TRA VÀ THOÁT SỚM ĐỂ TRỊ LỖI LINT ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Nếu chưa có quyền thì ghi log và THOÁT LUÔN khỏi hàm
                Log.d("DailyReminderWorker", "Chưa được cấp quyền gửi thông báo")
                return
            }
        }

        // Xuống được đến đây tức là (1) Android 12 trở xuống HOẶC (2) Android 13+ đã có quyền
        notificationManager.notify(1, notification)
    }
}