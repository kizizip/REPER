package com.ssafy.reper.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
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
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private val MICROPHONE_PERMISSION_REQUEST_CODE = 1002  // 마이크 권한 요청 코드 추가

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

        // 권한 체크 시작 - 카메라 권한부터 확인
        checkCameraPermission()
    }

    // 카메라 권한 확인 함수
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 카메라 권한이 없는 경우 권한 요청
            requestPermissions(
                arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            // 이미 카메라 권한이 있는 경우 마이크 권한 확인으로 진행
            Log.d(TAG, "checkCameraPermission: 카메라 권한 있음")
            checkMicrophonePermission()
        }
    }

    // 마이크 권한 확인 함수
    private fun checkMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 마이크 권한이 없는 경우 권한 요청
            requestPermissions(
                arrayOf(android.Manifest.permission.RECORD_AUDIO),
                MICROPHONE_PERMISSION_REQUEST_CODE
            )
        } else {
            // 이미 마이크 권한이 있는 경우
            Log.d(TAG, "checkMicrophonePermission: 마이크 권한 있음")
        }
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 카메라 권한이 승인된 경우
                    Log.d(TAG, "onRequestPermissionsResult: 카메라 권한 승인됨")
                    // 카메라 권한 승인 후 마이크 권한 확인
                    checkMicrophonePermission()
                } else {
                    // 카메라 권한이 거부된 경우
                    Toast.makeText(
                        this,
                        "원활한 기능을 위해 카메라 권한을 허용해 주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                    // 카메라 권한이 거부되어도 마이크 권한 확인
                    checkMicrophonePermission()
                }
            }
            MICROPHONE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 마이크 권한이 승인된 경우
                    Log.d(TAG, "onRequestPermissionsResult: 마이크 권한 승인됨")
                } else {
                    // 마이크 권한이 거부된 경우
                    Toast.makeText(
                        this,
                        "원활한 기능을 위해 마이크 권한을 허용해 주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
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