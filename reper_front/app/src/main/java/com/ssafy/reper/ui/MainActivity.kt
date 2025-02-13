package com.ssafy.reper.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.ssafy.reper.R
import com.ssafy.reper.base.ApplicationClass
import com.ssafy.reper.data.dto.UserToken
import com.ssafy.reper.data.local.SharedPreferencesUtil
import com.ssafy.reper.databinding.ActivityMainBinding
import com.ssafy.reper.ui.boss.BossViewModel
import com.ssafy.reper.ui.boss.NoticeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


private const val TAG = "MainActivity_싸피"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var backPressedTime: Long = 0    // 뒤로가기 버튼을 누른 시간 저장
    private val noticeViewModel: NoticeViewModel by viewModels()
    private val bossViewModel: BossViewModel by viewModels()
    private val fcmViewModel:FcmViewModel by viewModels()

    lateinit var sharedPreferencesUtil: SharedPreferencesUtil


    override fun onCreate(savedInstanceState: Bundle?) {

        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        sharedPreferencesUtil = SharedPreferencesUtil(applicationContext)

        // View Binding 초기화
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sendFCMFileUpload()

        val navController =
            supportFragmentManager.findFragmentById(R.id.activityMainFragmentContainer)
                ?.findNavController()
        navController?.let {
            binding.activityMainBottomMenu.setupWithNavController(it)
        }
        
        // FCM Token 비동기 처리
        CoroutineScope(Dispatchers.Main).launch {
            // 비동기적으로 백그라운드 스레드에서 토큰을 가져옴
            val token = withContext(Dispatchers.IO) {
                getFCMToken()
            }
            // 토큰을 받은 후 메인 스레드에서 UI 작업
            fcmViewModel.saveToken(UserToken(1, token, ApplicationClass.sharedPreferencesUtil.getUser().userId!!.toInt()))
            Log.d("FCMTOKEN", token)
        }

        // 📌 FCM에서 targetFragment 전달받았는지 확인 후, 해당 프래그먼트로 이동
        val targetFragment = intent.getStringExtra("targetFragment")
        val requestId = intent.getStringExtra("requestId")
        Log.d(TAG, "onCreate:전달하긴해..?프래그먼트 ${targetFragment}")
        Log.d(TAG, "onCreate:전달하긴해..?아이디..! ${requestId}")
        if (targetFragment != null) {
            when (targetFragment) {
                "OrderFragment" -> {
                    val orderId = intent.getStringExtra("requestId")!!.toInt()
                    val bundle = Bundle().apply {
                        putInt("orderId", orderId)  // orderId를 번들에 담기
                    }
                    navController?.navigate(R.id.orderFragment, bundle)
                }
                "WriteNoticeFragment" -> {
                    val noticeId = intent.getStringExtra("requestId")!!.toInt()
                    noticeViewModel.getNotice(1, requestId!!.toInt(), 1).also {
                        Log.d(TAG, "onCreate: ${targetFragment}")
                        noticeViewModel.clickNotice.observe(this) { notice ->
                            if (notice != null) {
                                navController?.navigate(R.id.writeNotiFragment)
                            }
                        }
                    }
                }
                "BossFragment" ->{
//                    sharedPreferencesUtil.addStore(intent.getStringExtra("requestId")!!.toInt())
                    navController?.navigate(R.id.bossFragment)
                }
                "RecipeManageFragment"->{
                    navController?.navigate(R.id.recipeManageFragment)
                }
                "MyPageFragment"->{
//                    sharedPreferencesUtil.addStore(intent.getStringExtra("requestId")!!.toInt())
                    navController?.navigate(R.id.myPageFragment)
                }
                "" -> navController?.navigate(R.id.bossFragment)
                else -> navController?.navigate(R.id.homeFragment) // 기본값
            }
        }

        // FCM Token 비동기 처리
        CoroutineScope(Dispatchers.Main).launch {
            val token = withContext(Dispatchers.IO) {
                getFCMToken()
            }
            fcmViewModel.saveToken(UserToken(1, token, ApplicationClass.sharedPreferencesUtil.getUser().userId!!.toInt()))
            Log.d("FCMTOKEN", token)
        }


    }

    // FCM 토큰을 비동기적으로 가져오는 함수
    private suspend fun getFCMToken(): String {
        return try {
            // FCM Token을 비동기적으로 가져옴
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e("FCM Error", "Fetching FCM token failed", e)
            ""
        }
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

    private fun sendFCMFileUpload(){
        bossViewModel.recipeLoad.observe(this) { result ->
            when (result) {
                "success" -> {
                   fcmViewModel.sendToUserFCM(1,"레시피 업로드 성공",sharedPreferencesUtil.getStateName(),"RecipeManageFragment",0)
                }
                "failure" -> {
                    fcmViewModel.sendToUserFCM(1,"레시피 업로드 실패",sharedPreferencesUtil.getStateName(),"RecipeManageFragment",0)
                }
            }
        }

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