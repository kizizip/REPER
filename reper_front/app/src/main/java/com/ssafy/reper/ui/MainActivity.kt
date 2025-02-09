package com.ssafy.reper.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.ssafy.reper.R
import com.ssafy.reper.databinding.ActivityMainBinding
import com.ssafy.reper.ui.home.HomeFragment
import com.ssafy.reper.ui.mypage.MyPageFragment
import com.ssafy.reper.ui.order.OrderFragment
import com.ssafy.reper.ui.recipe.AllRecipeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.ssafy.reper.data.dto.UserToken
import com.ssafy.reper.data.remote.RetrofitUtil
import com.ssafy.reper.ui.boss.BossViewModel
import com.ssafy.reper.ui.boss.NoticeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch

private const val TAG = "MainActivity_싸피"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var backPressedTime: Long = 0    // 뒤로가기 버튼을 누른 시간 저장
    private val noticeViewModel: NoticeViewModel by viewModels()
    private val bossViewModel: BossViewModel by viewModels()
    private val fcmViewModel: FcmViewModel by viewModels()

    var userId = 1
    var storeId = 1

    override fun onCreate(savedInstanceState: Bundle?) {

        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        // View Binding 초기화
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController =
            supportFragmentManager.findFragmentById(R.id.activityMainFragmentContainer)
                ?.findNavController()
        navController?.let {
            binding.activityMainBottomMenu.setupWithNavController(it)
        }

//
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
//                return@OnCompleteListener
//            }
//
//            val token = task.result
//            CoroutineScope(Dispatchers.IO).launch {
//                // 네트워크 작업이나 DB 저장 등 백그라운드에서 해야 하는 작업
//                fcmViewModel.saveToken(UserToken(storeId, token, userId))
//            }
//            Log.d("FCMTOKEN", token)
//        })

    }

    fun hideBottomNavigation() {
        binding.activityMainBottomMenu.visibility = View.GONE
    }

    fun showBottomNavigation() {
        binding.activityMainBottomMenu.visibility = View.VISIBLE
    }

    // binding의 bottomMenu에 접근하기 위한 public 메서드
    fun getBottomNavigationView(): BottomNavigationView {
        return binding.activityMainBottomMenu
    }


    // backstack에 아무것도없는 상태에서 뒤로가기 버튼을 눌렀을때
    //이거 컨트롤러랑 같이 쓸수없음,,,,supportFragmentManager는 컨트롤러 안의 백스텍을 세는게아니라서,..
//    @Deprecated("Deprecated in Java")
//    override fun onBackPressed() {
//        // 현재 BackStack에 있는 Fragment 개수 확인
//        if (supportFragmentManager.backStackEntryCount == 0) {
//            // 2초 이내에 뒤로가기 버튼을 한 번 더 누르면 앱 종료
//            if (System.currentTimeMillis() - backPressedTime < 2000) {
//                finish()
//                return
//            }
//
//            // 뒤로가기 버튼을 처음 누를 때
//            Toast.makeText(this, "한 번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
//            backPressedTime = System.currentTimeMillis()
//        } else {
//            super.onBackPressed()
//        }
//    }
}