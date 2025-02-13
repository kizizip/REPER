import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.navigation.findNavController
import com.ssafy.reper.R
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.boss.BossFragment

private const val TAG = "BossFragmentReceiver"
class FragmentReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive 호출됨: ${intent?.action}")
        
        when (intent?.action) {
            "com.ssafy.reper.UPDATE_BOSS_FRAGMENT" -> {
                handleBossFragmentUpdate(context, intent)
            }
            "com.ssafy.reper.DELETE_ACCESS" -> {
                handleDeleteAccess(context, intent)
            }
        }
    }

    private fun handleBossFragmentUpdate(context: Context?, intent: Intent) {
        try {
            val storeId = intent.getStringExtra("requestId")
            val activity = context as? MainActivity
            val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.activityMainFragmentContainer)
            val currentFragment = navHostFragment?.childFragmentManager?.fragments?.firstOrNull() as? BossFragment

            currentFragment?.let {
                it.bossViewModel.getAllEmployee(storeId!!.toInt())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in handleBossFragmentUpdate: ${e.message}")
        }
    }

    private fun handleDeleteAccess(context: Context?, intent: Intent) {
        try {
            val activity = context as? MainActivity
            // UI 스레드에서 실행
            activity?.runOnUiThread {
                // 권한 삭제 관련 다이얼로그 또는 UI 업데이트 처리
                val dialog = AlertDialog.Builder(activity)
                    .setTitle("권한 삭제")
                    .setMessage("해당 가게에 대한 권한이 삭제되었습니다.")
                    .setPositiveButton("확인") { dialog, _ ->
                        dialog.dismiss()
                        // MyPageFragment로 이동
                        activity.findNavController(R.id.activityMainFragmentContainer)
                            .navigate(R.id.myPageFragment)
                    }
                    .setCancelable(false)
                    .create()
                dialog.show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in handleDeleteAccess: ${e.message}")
        }
    }
} 