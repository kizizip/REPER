package com.ssafy.reper.base

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.TextView
import android.util.Log
import androidx.navigation.findNavController
import com.ssafy.reper.R
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.boss.BossFragment

private const val TAG = "BossFragmentReceiver"
class FragmentReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "FragmentReceiver onReceive called")
        Log.d(TAG, "Action: ${intent?.action}")
        Log.d(TAG, "Extras: ${intent?.extras}")
        
        when (intent?.action) {
            "com.ssafy.reper.DELETE_ACCESS" -> {
                Log.d(TAG, "DELETE_ACCESS action received")
                handleDeleteAccess(context, intent)
            }
            "com.ssafy.reper.UPDATE_BOSS_FRAGMENT" -> {
                Log.d(TAG, "UPDATE_BOSS_FRAGMENT action received")
                val requestId = intent.getStringExtra("requestId")
                Log.d(TAG, "RequestId: $requestId")
                handleBossFragmentUpdate(context, intent)
            }
            else -> {
                Log.d(TAG, "Unknown action received: ${intent?.action}")
            }
        }
    }

    private fun handleBossFragmentUpdate(context: Context?, intent: Intent) {
        try {
            val requestId = intent.getStringExtra("requestId")
            Log.d(TAG, "Updating BossFragment with requestId: $requestId")
            
            val activity = context as? MainActivity
            val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.activityMainFragmentContainer)
            val currentFragment = navHostFragment?.childFragmentManager?.fragments?.firstOrNull() as? BossFragment
            
            currentFragment?.let {
                Log.d(TAG, "BossFragment found, updating...")
                it.bossViewModel.getAllEmployee(requestId!!.toInt())
                Log.d(TAG, "BossFragment update completed")
            } ?: Log.e(TAG, "BossFragment not found")
        } catch (e: Exception) {
            Log.e(TAG, "Error in handleBossFragmentUpdate: ${e.message}")
            e.printStackTrace()
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