package com.ssafy.reper.base


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.ssafy.reper.R
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.home.HomeFragment

private const val TAG = "FragmentReceiver"

class FragmentReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "FragmentReceiver onReceive called")
        Log.d(TAG, "Action: ${intent?.action}")
        Log.d(TAG, "Extras: ${intent?.extras}")

        when (intent?.action) {
            //권한삭제알림을 받았을때 -> 스토어 리스트갱신, 현재 그가게를 이용중이면 다이얼로그
            "com.ssafy.reper.DELETE_ACCESS" -> {
                Log.d(TAG, "DELETE_ACCESS action received")
                Log.d(TAG, "onReceive: ${intent.data}")
                val requestId = intent?.getStringExtra("requestId")
                Log.d(TAG, "RequestId from intent: $requestId")
                try {
                    MainActivity.instance?.let { activity ->
                        Log.d(TAG, "스토어 리스트 갱신")
                        activity.runOnUiThread {
                            if (ApplicationClass.sharedPreferencesUtil.getStoreId() == requestId!!.toInt()){
                                Log.d(TAG, "onReceive:지금의 아이디 ${ApplicationClass.sharedPreferencesUtil.getStoreId()} &&받아온아이디${requestId}")
                                activity.showDeleteDialog(requestId!!.toInt())
                            }
                            activity.refreshStoreList()
                        }
                    } ?: run {
                        Log.e(TAG, "MainActivity instance is null")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error refreshing store list: ${e.message}")
                    e.printStackTrace()
                }
            }

            //권한요청, 직원삭제 -> 가게 직원리스트 갱신
            "com.ssafy.reper.UPDATE_BOSS_FRAGMENT" -> {
                Log.d(TAG, "UPDATE_BOSS_FRAGMENT action received")
                val requestId = intent.getStringExtra("requestId")
                Log.d(TAG, "RequestId: $requestId")
                try {
                    MainActivity.instance?.let { activity ->
                        if (ApplicationClass.sharedPreferencesUtil.getStoreId() == requestId!!.toInt()){
                            Log.d(TAG, "직원 리스트 갱신")
                            activity.runOnUiThread {
                                activity.refreshEmployeeList(requestId!!.toInt())
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

            //권한 승인 개인 스토어 리스트 갱신
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

            //주문알림
            "com.ssafy.reper.UPDATE_ORDER" -> {
                Log.d(TAG, "UPDATE_ORDER")
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

            //공지알림
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

}