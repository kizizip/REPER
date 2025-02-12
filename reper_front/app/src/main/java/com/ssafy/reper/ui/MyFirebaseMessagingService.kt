package com.ssafy.reper.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ssafy.reper.R

private const val TAG = "MyFirebaseMessagingServ"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // 새 토큰이 발급될 때 호출되는 메서드
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("MyFirebaseMessagingService", "Refreshed token: $token")
    }

    // FCM 메시지를 수신할 때 호출되는 메서드
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage.data // 데이터 메시지
        val notification = remoteMessage.notification // 알림 메시지

        // 로그 출력
        if (data.isNotEmpty()) {
            Log.d("MyFirebaseMessagingService", "Data Message: $data")
        }

        notification?.let {
            val title = it.title ?: "No title"
            val body = it.body ?: "No body"
            Log.d("MyFirebaseMessagingService", "Notification Message: Title=$title, Body=$body")

            // 데이터 메시지도 함께 전달
            sendNotification(title, body, data)
        }
    }


    private fun sendNotification(title: String, body: String, data: Map<String, String>) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Log.d(TAG, "sendNotification: ${data}")

        // Android 8.0 이상 알림 채널 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "default_channel"
            val channelName = "Default Notifications"
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Notifications for FCM messages"
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 500, 500)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 이동할 프래그먼트 결정 (예: "OrderFragment")
        val targetFragment = data["targetFragment"] ?: "HomeFragment"
        val requestId = data["requestId"] ?: "0"

        // MainActivity를 열면서 targetFragment 정보 전달
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("targetFragment", targetFragment)
            putExtra("requestId", requestId)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 알림 생성
        val notification = NotificationCompat.Builder(this, "default_channel")
            .setSmallIcon(R.drawable.mypage_bell_btn)  // 작은 아이콘 지정
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Heads-up 알림 활성화
            .setDefaults(Notification.DEFAULT_ALL) // 소리 및 진동
            .setAutoCancel(true)
            .setContentIntent(pendingIntent) // 클릭 시 화면 이동
            .build()

        notificationManager.notify(0, notification)
    }

}
