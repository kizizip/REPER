package com.ssafy.reper.ui

import FcmDialog
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.app.Dialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ssafy.reper.R
import com.ssafy.reper.ui.boss.BossViewModel

private const val TAG = "MyFirebaseMessagingServ"

class MyFirebaseMessagingService : FirebaseMessagingService() {


    // 새 토큰이 발급될 때 호출되는 메서드
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("MyFirebaseMessagingService", "Refreshed token: $token")
    }

    // FCM 메시지를 수신할 때 호출되는 메서드
    @SuppressLint("UnsafeImplicitIntentLaunch")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM Message Received")
        Log.d(TAG, "From: ${remoteMessage.from}")
        Log.d(TAG, "Data: ${remoteMessage.data}")
        Log.d(TAG, "Notification: ${remoteMessage.notification}")

        val data = remoteMessage.data
        val notification = remoteMessage.notification
        
        Log.d(TAG, "FCM Data: $data")
        Log.d(TAG, "알림수신으로 들어온 데이타targetFragment: ${data["targetFragment"]}, requestId: ${data["requestId"]}")

        // 로그 추가
        Log.d(TAG, "Received FCM with targetFragment: ${data["targetFragment"]}")

        val title = notification?.title
        val body = notification?.body
        Log.d(TAG, "FCM Notification Title: $title")
        Log.d(TAG, "FCM Notification Body: $body")


        // BossFragment 갱신이 필요한 경우 브로드캐스트 발송
        if (data["targetFragment"] == "BossFragment") {
            try {
                val updateIntent = Intent("com.ssafy.reper.UPDATE_BOSS_FRAGMENT").apply {
                    putExtra("requestId", data["requestId"])
                    addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                }
                applicationContext.sendBroadcast(updateIntent)
                Log.d(TAG, "BossFragment 브로드캐스트 발송 성공")
            } catch (e: Exception) {
                Log.e(TAG, "BossFragment 브로드캐스트 발송 실패: ${e.message}")
            }
        }


        // OrderList 갱신이 필요한 경우 브로드캐스트 발송
        if (data["targetFragment"] == "OrderRecipeFragment") {
            try {
                val updateIntent = Intent("com.ssafy.reper.UPDATE_ORDER")
                // LocalBroadcastManager 사용
                LocalBroadcastManager.getInstance(applicationContext)
                    .sendBroadcast(updateIntent)
                Log.d(TAG, "Order update LocalBroadcast 발송 성공")
            } catch (e: Exception) {
                Log.e(TAG, "Order update LocalBroadcast 발송 실패: ${e.message}")
            }
        }

        // 권한 삭제 알림인 경우
        if (data["targetFragment"] == "MyPageFragment" && title == "권한 삭제알림") {
            Log.d(TAG, "권한 삭제 알림 수신")
            try {
                Log.d(TAG, "권한 삭제 브로드캐스트 발송 시도")
                val updateIntent = Intent("com.ssafy.reper.DELETE_ACCESS").apply {
                    putExtra("title", title)
                    putExtra("body", body)
                    putExtra("requestId", data["requestId"])  // FCM 메시지의 내용
                    `package` = packageName
                    addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                applicationContext.sendBroadcast(updateIntent)
                Log.d(TAG, "권한 삭제 브로드캐스트 발송 성공")
            } catch (e: Exception) {
                Log.e(TAG, "권한 삭제 브로드캐스트 발송 실패: ${e.message}")
                e.printStackTrace()
            }

            notification?.let {
                val title = it.title ?: "No title"
                val body = it.body ?: "No body"
                Log.d(TAG, "알림 메시지: Title=$title, Body=$body")

                // 데이터 메시지도 함께 전달
                sendNotification(title, body, data)
            }

        }

        // 공지사항 갱신이 필요한 경우 브로드캐스트 발송
        if (data["targetFragment"] == "WriteNoticeFragment") {
            try {
                val updateIntent = Intent("com.ssafy.reper.UPDATE_NOTICE").apply {
                    putExtra("requestId", data["requestId"])
                }
                // LocalBroadcastManager 사용
                LocalBroadcastManager.getInstance(applicationContext)
                    .sendBroadcast(updateIntent)
                Log.d(TAG, "Notice list update LocalBroadcast 발송 성공: ${data["requestId"]}")
            } catch (e: Exception) {
                Log.e(TAG, "Notice list update LocalBroadcast 발송 실패: ${e.message}")
            }
        }


        // 권한 허락시 스토어 리스트 갱신 필요한 경우 브로드캐스트 발송
        if (data["targetFragment"] == "MyPageFragment" && title == "권한 허가 알림" ) {
            try {
                val updateIntent = Intent("com.ssafy.reper.APPROVE_ACCESS").apply {
                    putExtra("requestId", data["requestId"])
                }
                // LocalBroadcastManager 사용
                LocalBroadcastManager.getInstance(applicationContext)
                    .sendBroadcast(updateIntent)
                Log.d(TAG, "onMessageReceived: 허가알림 브로드 캐스트 발송")
            } catch (e: Exception) {
                Log.e(TAG, "Notice list update LocalBroadcast 발송 실패: ${e.message}")
            }
        }


        // 일반 알림 처리
        notification?.let {
            Log.d(TAG, "일반 알림 수신")
            val title = it.title ?: "알림"
            val body = it.body ?: ""
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
            putExtra("title",title)
            putExtra("body",body)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 알림 생성
        val notification = NotificationCompat.Builder(this, "default_channel")
            .setSmallIcon(R.drawable.after_move_coffe)  // 작은 아이콘 지정
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
