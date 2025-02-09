package com.ssafy.reper.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // 새 토큰이 발급될 때 호출되는 메서드
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("MyFirebaseMessagingService", "Refreshed token: $token")
    }

    // FCM 메시지를 수신할 때 호출되는 메서드
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // 알림 메시지나 데이터 메시지를 처리합니다.
        if (remoteMessage.data.isNotEmpty()) {
            // 데이터 메시지 처리 (여기서는 단순히 로그로 출력)
            Log.d("MyFirebaseMessagingService", "Data Message: ${remoteMessage.data}")
        }

        // 알림 메시지가 포함된 경우
        remoteMessage.notification?.let {
            val title = it.title ?: "No title"
            val body = it.body ?: "No body"
            Log.d("MyFirebaseMessagingService", "Notification Message: Title=$title, Body=$body")
            sendNotification(title, body)
        }
    }

    // 알림을 화면에 표시하는 메서드
    private fun sendNotification(title: String, body: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android 8.0 이상에서는 알림 채널을 설정해야 합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "default_channel"
            val channelName = "Default Notifications"
            val channelDescription = "Notifications for FCM messages"

            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = channelDescription
            notificationManager.createNotificationChannel(channel)
        }

        // 알림 객체 생성
        val notification = NotificationCompat.Builder(this, "default_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info) // 알림 아이콘 설정
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // 알림 표시
        notificationManager.notify(0, notification)
    }
}
