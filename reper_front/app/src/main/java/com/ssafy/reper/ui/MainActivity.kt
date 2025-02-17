package com.ssafy.reper.ui


import MainActivityViewModel
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.os.Build
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
import com.ssafy.reper.base.FragmentReceiver
import com.ssafy.reper.data.dto.UserToken
import com.ssafy.reper.data.local.SharedPreferencesUtil
import com.ssafy.reper.databinding.ActivityMainBinding
import com.ssafy.reper.ui.boss.BossViewModel
import com.ssafy.reper.ui.boss.NoticeViewModel
import com.ssafy.reper.ui.home.StoreViewModel
import com.ssafy.reper.ui.order.OrderViewModel
import com.ssafy.reper.util.ViewModelSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.lifecycle.ViewModelProvider
import com.ssafy.reper.base.ApplicationClass.Companion.sharedPreferencesUtil
import com.ssafy.reper.ui.home.HomeFragment
import kotlin.math.log


private const val TAG = "MainActivity_싸피"

class MainActivity : AppCompatActivity() {

    companion object {
        var instance: MainActivity? = null
            private set
    }

    private lateinit var binding: ActivityMainBinding
    private var backPressedTime: Long = 0    // 뒤로가기 버튼을 누른 시간 저장
    val noticeViewModel: NoticeViewModel by viewModels()
    private val bossViewModel: BossViewModel by viewModels()
    private val fcmViewModel: FcmViewModel by viewModels()
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private val MICROPHONE_PERMISSION_REQUEST_CODE = 1002  // 마이크 권한 요청 코드 추가

    private val mainViewModel: MainActivityViewModel by lazy { ViewModelSingleton.mainActivityViewModel }
    private val storeViewModel: StoreViewModel by viewModels()
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil
    var sharedUserId = 0
    var sharedStoreId = 0
    private lateinit var receiver: FragmentReceiver
    val orderViewModel: OrderViewModel by viewModels()

    private val orderReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.ssafy.reper.UPDATE_ORDER_FRAGMENT" -> {
                    Log.d(TAG, "Order update received in MainActivity")
                    // 여기서 한 번만 호출하면 두 프래그먼트 모두 갱신됨
                    orderViewModel.getOrders()
                }
            }
        }
    }


    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        instance = this

        // View Binding 초기화
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // 화면 회전 잠금

        sharedPreferencesUtil = SharedPreferencesUtil(applicationContext)
        sharedUserId = sharedPreferencesUtil.getUser().userId!!.toInt()
        sharedStoreId = sharedPreferencesUtil.getStoreId()

        mainViewModel.setUserInfo(sharedUserId)
        mainViewModel.getIsEmployee(sharedUserId)
        mainViewModel.getLikeRecipes(sharedStoreId, sharedUserId)
        mainViewModel.getRecipeList()

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
            fcmViewModel.saveToken(
                UserToken(
                    sharedPreferencesUtil.getStoreId(),
                    token,
                    sharedPreferencesUtil.getUser().userId!!.toInt()
                )
            )
            Log.d("FCMTOKEN", token)
        }

        // 📌 FCM에서 targetFragment 전달받았는지 확인 후, 해당 프래그먼트로 이동
        val targetFragment = intent.getStringExtra("targetFragment")
        val requestId = intent.getStringExtra("requestId")?.toInt()
        if (targetFragment != null) {
            Log.d(TAG, "onCreate: ${targetFragment}")
            when (targetFragment) {
                "OrderRecipeFragment" -> {
                    val orderId = intent.getStringExtra("requestId")!!.toInt()
                    val bundle = Bundle().apply {
                        putInt("orderId", orderId)  // orderId를 번들에 담기
                    }
                    navController?.navigate(R.id.orderRecipeFragment, bundle)
                }

                "WriteNoticeFragment" -> {
                    navController?.navigate(R.id.noticeManageFragment)
                }

                "BossFragment" -> {
                    sharedPreferencesUtil.setStoreId(requestId)
                    navController?.navigate(R.id.bossFragment)
                    Log.d(TAG, "onCreate: ${requestId}승인요청 가게 아이디")
                    bossViewModel.getAllEmployee(requestId!!)
                    Log.d(TAG, "onCreate: ${bossViewModel.waitingList}")
                    navController?.navigate(R.id.bossFragment)

                }

                "RecipeManageFragment" -> {
                    navController?.navigate(R.id.recipeManageFragment)
                }

                "MyPageFragment" -> {
                    sharedPreferencesUtil.setStoreId(requestId)
                    navController?.navigate(R.id.myPageFragment)
                    if (requestId == sharedPreferencesUtil.getStoreId()) {
                        storeViewModel.getUserStore(sharedUserId)
                    }
                }

                else -> navController?.navigate(R.id.homeFragment) // 기본값
            }
        }


        // 권한 체크 시작 - 카메라 권한부터 확인
        checkCameraPermission()


        // BossFragmentReceiver 등록
        receiver = FragmentReceiver()
        val filter = IntentFilter().apply {
            addAction("com.ssafy.reper.UPDATE_BOSS_FRAGMENT")
            addAction("com.ssafy.reper.DELETE_ACCESS")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(receiver, filter, RECEIVER_EXPORTED)
        }

        // 리시버 등록
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                orderReceiver,
                IntentFilter("com.ssafy.reper.UPDATE_ORDER_FRAGMENT"),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            registerReceiver(
                orderReceiver, IntentFilter("com.ssafy.reper.UPDATE_ORDER_FRAGMENT"),
                RECEIVER_NOT_EXPORTED
            )
        }

        // BroadcastReceiver 등록
        LocalBroadcastManager.getInstance(this).registerReceiver(
            receiver,
            IntentFilter("com.ssafy.reper.UPDATE_NOTICE")
        )

        LocalBroadcastManager.getInstance(this).registerReceiver(
            receiver,
            IntentFilter("com.ssafy.reper.APPROVE_ACCESS")
        )

        LocalBroadcastManager.getInstance(this).registerReceiver(
            receiver,
            IntentFilter("com.ssafy.reper.UPDATE_ORDER")
        )


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

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        // 리시버 해제
        try {
            unregisterReceiver(receiver)
        } catch (e: Exception) {
            Log.e(TAG, "Error unregistering receiver: ${e.message}")
        }
        unregisterReceiver(orderReceiver)
        // BroadcastReceiver 해제
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    // FCM 토큰을 비동기적으로 가져오는 함수
    suspend fun getFCMToken(): String {
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

    private var isObserverRegistered = false // 옵저버 등록 상태를 추적하는 변수

    private fun sendFCMFileUpload() {
        var lastResult: String? = null // 마지막 상태를 저장할 변수
        Log.d(TAG, "sendFCMFileUpload: 마지막 상태 ${lastResult}")
        Log.d(TAG, "sendFCMFileUpload: 뷰모델안 상태${bossViewModel.recipeLoad.value}")

        if (!isObserverRegistered) { // 옵저버가 등록되지 않은 경우에만 등록
            bossViewModel.recipeLoad.observe(this) { result ->
                if (lastResult != result) { // 값이 바뀌었을 때만 실행
                    when (result) {
                        "success" -> {
                            fcmViewModel.sendToUserFCM(
                                sharedUserId,
                                "레시피 업로드 성공",
                                sharedPreferencesUtil.getStateName(),
                                "RecipeManageFragment",
                                0
                            )
                        }

                        "failure" -> {
                            fcmViewModel.sendToUserFCM(
                                sharedUserId,
                                "레시피 업로드 실패",
                                sharedPreferencesUtil.getStateName(),
                                "RecipeManageFragment",
                                0
                            )
                            Log.d(
                                TAG,
                                "sendFCMFileUpload: 알림이 확인후${bossViewModel.recipeLoad.value}"
                            )
                        }
                    }
                    lastResult = result // 마지막 결과를 갱신
                }
            }
            isObserverRegistered = true // 옵저버 등록 상태 업데이트
        }
    }


    // 공지사항 리스트 갱신 메서드
    fun refreshNoticeList() {
        val storeId = ApplicationClass.sharedPreferencesUtil.getStoreId()
        val userId = ApplicationClass.sharedPreferencesUtil.getUser().userId!!.toInt()
        Log.d(TAG, "Refreshing notice list from MainActivity - storeId: $storeId, userId: $userId")
        noticeViewModel.getAllNotice(storeId, userId)
    }


    // 주문 리스트 갱신 메서드
    fun refreshOrderList() {
        Log.d(TAG, "refreshOrderList: 주문 리스트 갱신 시작")
        //DB에 반영되기 전에 알림이 먼저오나...? 오더아이디가 -1된상태로 오게됨 ㅠㅠ
        //알림 클릭도 바로누르면 안되고 시간이 좀 지나야 올바른 곳으로 가게됨...서버와 상의를 해봐야할거같아유
        orderViewModel.getOrders()
    }

    // 스토어 리스트 갱신 메서드
    fun refreshStoreList() {
        val userId = ApplicationClass.sharedPreferencesUtil.getUser().userId!!.toInt()
        storeViewModel.getUserStore(userId)
        Log.d(TAG, "refreshStoreList: ${storeViewModel.myStoreList.value}")
        if (storeViewModel.myStoreList.value==null||storeViewModel.myStoreList.value!!.size == 0){
            ApplicationClass.sharedPreferencesUtil.setStoreId(0)
        }
    }
}