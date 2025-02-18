package com.ssafy.reper.base

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build.VERSION_CODES.S
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.util.Log
import androidx.navigation.findNavController
import com.ssafy.reper.R
import com.ssafy.reper.data.local.SharedPreferencesUtil
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.boss.BossFragment
import com.ssafy.reper.ui.boss.WriteNotiFragment
import com.ssafy.reper.ui.home.HomeFragment

private const val TAG = "FragmentReceiver"
class FragmentReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "FragmentReceiver onReceive called")
        Log.d(TAG, "Action: ${intent?.action}")
        Log.d(TAG, "Extras: ${intent?.extras}")

        when (intent?.action) {
            "com.ssafy.reper.DELETE_ACCESS" -> {
                Log.d(TAG, "DELETE_ACCESS action received")
                val requestId = intent.getStringExtra("requestId")
//                handleDeleteAccess(context, intent)
                try {
                    MainActivity.instance?.let { activity ->
                        Log.d(TAG, "스토어 리스트 갱신")
                        activity.runOnUiThread {
                            activity.refreshStoreList()
                            activity.refreshOrderList()
                            activity.refreshEmployeeList(requestId!!.toInt())
                            // 여기서 showDeleteDialog 호출
                            Handler(Looper.getMainLooper()).post {
                                Log.d(TAG, "🔴 showDeleteDialog 실행 직전 - storeId: $requestId")
                                activity.showDeleteDialog(requestId.toInt())
                            }
                        }
                    } ?: run {
                        Log.e(TAG, "MainActivity instance is null")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error refreshing store list: ${e.message}")
                    e.printStackTrace()
                }
            }

            "com.ssafy.reper.UPDATE_BOSS_FRAGMENT" -> {
                Log.d(TAG, "UPDATE_BOSS_FRAGMENT action received")
                val requestId = intent.getStringExtra("requestId")
                Log.d(TAG, "RequestId: $requestId")
                try {
                    MainActivity.instance?.let { activity ->
                        Log.d(TAG, "직원 리스트 갱신")
                        activity.runOnUiThread {
                            activity.refreshEmployeeList(requestId!!.toInt())
                        }
                    } ?: run {
                        Log.e(TAG, "MainActivity instance is null")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error refreshing store list: ${e.message}")
                    e.printStackTrace()
                }
            }

            "com.ssafy.reper.APPROVE_ACCESS" -> {
                Log.d(TAG, "APPROVE_ACCESS action received")
                try {
                    MainActivity.instance?.let { activity ->
                        Log.d(TAG, "MainActivity instance found, refreshing store list")
                        activity.runOnUiThread {
                            activity.refreshStoreList()
                            // HomeFragment 찾아서 스피너 갱신
                            val navHostFragment = activity.supportFragmentManager
                                .findFragmentById(R.id.activityMainFragmentContainer)
                            val currentFragment = navHostFragment
                                ?.childFragmentManager
                                ?.fragments
                                ?.firstOrNull()
                            
                            if (currentFragment is HomeFragment) {
                                Log.d(TAG, "Current fragment is HomeFragment, updating spinner")
                                currentFragment.initSpinner()
                            }
                        }
                    } ?: Log.e(TAG, "MainActivity instance is null")
                } catch (e: Exception) {
                    Log.e(TAG, "Error refreshing store list: ${e.message}")
                    e.printStackTrace()
                }
            }

            "com.ssafy.reper.UPDATE_ORDER"->{  Log.d(TAG, "UPDATE_ORDER")
                try {
                    MainActivity.instance?.let { activity ->
                        Log.d(TAG, "오더리스트 갱신")
                        activity.runOnUiThread {
                            activity.refreshOrderList()
                        }
                    } ?: run {
                        Log.e(TAG, "MainActivity instance is null")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error refreshing order list: ${e.message}")
                    e.printStackTrace()
                }

            }

            "com.ssafy.reper.UPDATE_NOTICE" -> {
                Log.d(TAG, "UPDATE_NOTICE action received")
                try {
                    MainActivity.instance?.let { activity ->
                        Log.d(TAG, "MainActivity instance found, refreshing notice list")
                        activity.runOnUiThread {
                            activity.refreshNoticeList()
                        }
                    } ?: run {
                        Log.e(TAG, "MainActivity instance is null")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error refreshing notice list: ${e.message}")
                    e.printStackTrace()
                }
            }

            else -> {
                Log.d(TAG, "Unknown action received: ${intent?.action}")
            }
        }
    }


    private fun handleDeleteAccess(context: Context?, intent: Intent) {
        Log.d(TAG, "handleDeleteAccess started")
        try {
            val activity = context as? MainActivity
            if (activity == null) {
                Log.e(TAG, "Activity is null")
                return
            }

            val messageBody = intent.getStringExtra("body") ?: "권한이 삭제되었습니다."

            Log.d(TAG, "Activity found, showing dialog with message: $messageBody")
            activity.runOnUiThread {
                try {
                    // 커스텀 다이얼로그 생성
                    val dialog = AlertDialog.Builder(activity)
                        .setTitle("권한 삭제")
                        .setMessage(messageBody)
                        .setPositiveButton("확인") { dialog, _ ->
                            dialog.dismiss()
                            activity.findNavController(R.id.activityMainFragmentContainer)
                                .navigate(R.id.homeFragment)
                        }
                        .setCancelable(false)
                        .create()
                    dialog.show()
                    Log.d(TAG, "Dialog shown successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Error showing dialog: ${e.message}")
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in handleDeleteAccess: ${e.message}")
            e.printStackTrace()
        }
    }
} 