package com.ssafy.reper.ui


import MainActivityViewModel
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import androidx.lifecycle.Observer
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
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController

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

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.activityMainFragmentContainer) as? NavHostFragment
        val navController = navHostFragment?.navController

        navController?.let {
            binding.activityMainBottomMenu.setupWithNavController(it)
        }

// 바텀 네비게이션이 보이는 프래그먼트 목록 정의
        val bottomNavFragments = setOf(
            R.id.homeFragment,
            R.id.allRecipeFragment,
            R.id.orderFragment,
            R.id.myPageFragment
        )

// 현재 프래그먼트에 따라 뒤로 가기 버튼 동작 변경
        navController?.addOnDestinationChangedListener { _, destination, _ ->
            val isBottomNavVisible = bottomNavFragments.contains(destination.id)

            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isBottomNavVisible) {
                        // 바텀 네비게이션이 보이는 경우
                        if (navController.currentDestination?.id != R.id.homeFragment) {
                            navController.navigate(R.id.homeFragment)
                        } else {
                            finish()
                        }
                    } else {
                        // 바텀 네비게이션이 없는 경우 → 백 스택에서 이전 화면으로 이동
                        if (!navController.popBackStack()) {
                            // 백 스택에 남은 게 없으면 종료
                            finish()
                        }
                    }
                }
            })
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
                    Log.d(TAG, "onCreate: 일단 여기는옵ㄴ니다")
                    noticeViewModel.getNotice(sharedStoreId, requestId!!.toInt(), sharedUserId)
                    noticeViewModel.clickNotice.observe(this) { result ->
                        if (result !=null) {
                            Log.d(TAG, "onCreatenoti: $result")
                            navController?.navigate(R.id.writeNotiFragment)
                        } else {
                            Log.d(TAG, "onCreatenoti: $result")
                        }
                    }
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
                    bossViewModel.setRecipeLoad("fcm")
                    val title = intent.getStringExtra("title")
                    val body = intent.getStringExtra("body")
                    bossViewModel.fcmTitle = title!!
                    bossViewModel.fcmBody = body!!

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

        LocalBroadcastManager.getInstance(this).registerReceiver(
            receiver,
            IntentFilter("com.ssafy.reper.DELETE_ACCESS")
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


    private fun sendFCMFileUpload() {
        Log.d(TAG, "sendFCMFileUpload: 뷰모델안 상태${bossViewModel.recipeLoad.value}")
        bossViewModel.recipeLoad.observe(this) { result ->
            when (result) {
                "success" -> {
                    if (bossViewModel.uploadNum != 0) {
                        fcmViewModel.sendToUserFCM(
                            sharedUserId,
                            "${bossViewModel.fileName}",
                            "${bossViewModel.uploadNum}개의 레시피 업로드를 성공했습니다",
                            "RecipeManageFragment",
                            0
                        )
                        sharedPreferencesUtil.setFileNum(bossViewModel.uploadNum)
                        sharedPreferencesUtil.setFileState("success")
                        sharedPreferencesUtil.setFileName(bossViewModel.fileName)
                    } else {
                        fcmViewModel.sendToUserFCM(
                            sharedUserId,
                            "${bossViewModel.fileName}",
                            "레시피 업로드 실패\n파일형식을 확인해주세요",
                            "RecipeManageFragment",
                            0
                        )
                        sharedPreferencesUtil.setFileNum(bossViewModel.uploadNum)
                        sharedPreferencesUtil.setFileState("failure")
                        sharedPreferencesUtil.setFileName(bossViewModel.fileName)
                    }
                }

                "failure" -> {
                    fcmViewModel.sendToUserFCM(
                        sharedUserId,
                        "${bossViewModel.fileName}",
                        "파일 업로드에 실패했습니다.",
                        "RecipeManageFragment",
                        0
                    )
                    sharedPreferencesUtil.setFileNum(bossViewModel.uploadNum)
                    sharedPreferencesUtil.setFileState("failure")
                    sharedPreferencesUtil.setFileName(bossViewModel.fileName)
                    Log.d(
                        TAG,
                        "sendFCMFileUpload: 알림이 확인후${bossViewModel.recipeLoad.value}"
                    )
                }
            }
        }
    }


    // 공지사항 리스트 갱신 메서드
    fun refreshNoticeList() {
        val storeId = ApplicationClass.sharedPreferencesUtil.getStoreId()
        val userId = ApplicationClass.sharedPreferencesUtil.getUser().userId!!.toInt()
        Log.d(TAG, "Refreshing notice list from MainActivity - storeId: $storeId, userId: $userId")
        noticeViewModel.getAllNotice(storeId, userId)
    }


    fun refreshEmployeeList(storeId: Int) {
            bossViewModel.getAllEmployee(storeId)
    }


    // 주문 리스트 갱신 메서드
    fun refreshOrderList() {
        Log.d(TAG, "refreshOrderList: 주문 리스트 갱신 시작")
        orderViewModel.getOrders()
    }

    // 스토어 리스트 갱신 메서드
    fun refreshStoreList() {
        val userId = ApplicationClass.sharedPreferencesUtil.getUser().userId!!.toInt()
        storeViewModel.getUserStore(userId)
        storeViewModel.myStoreList.observe(this, Observer {
            Log.d(TAG, "refreshStoreList: 등록될때${storeViewModel.myStoreList.value}")
            if (storeViewModel.myStoreList.value == null || storeViewModel.myStoreList.value!!.size == 0) {
                ApplicationClass.sharedPreferencesUtil.setStoreId(0)
            }
        })

    }

    fun showDeleteDialog(storeId: Int) {
        Log.d(TAG, "showDeleteDialog: 불리고있나?")
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // 스토어 정보를 받아올 때까지 대기
                val store = withContext(Dispatchers.IO) {
                    storeViewModel.getStore(storeId)
                }

                val dialog = Dialog(this@MainActivity)
                dialog.setContentView(R.layout.dialog_delete_acccess)
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)


                Log.d(TAG, "showDeleteDialog: ${sharedStoreId}나의 현재 아이디, ${storeId}")

                if (store != null) {
                    dialog.findViewById<TextView>(R.id.dialog_delete_bold_tv).text =
                        "${store.storeName}"
                    dialog.findViewById<TextView>(R.id.dialog_delete_rle_tv).text =
                        "${sharedPreferencesUtil.getUser().username}님의 권한을"

                    dialog.findViewById<View>(R.id.dialog_delete_delete_btn).setOnClickListener {
                        dialog.dismiss()
                        val navHostFragment =
                            supportFragmentManager.findFragmentById(R.id.activityMainFragmentContainer) as NavHostFragment
                        val navController = navHostFragment.navController
                        navController.navigate(R.id.homeFragment)
                        sharedPreferencesUtil.setStoreId(0)
                    }

                    dialog.show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "오류가 발생했습니다: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                Log.d(TAG, "showDeleteDialog: ${e.message}")
            }
        }
    }
}