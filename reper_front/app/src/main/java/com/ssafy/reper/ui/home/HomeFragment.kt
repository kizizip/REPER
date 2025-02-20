package com.ssafy.reper.ui.home


import MainActivityViewModel
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.reper.R
import com.ssafy.reper.data.local.HomeBannerModel
import com.ssafy.reper.data.local.HomeLikeRecipeModel
import com.ssafy.reper.databinding.FragmentHomeBinding
import com.ssafy.reper.ui.home.adapter.RVHomeBannerAdapter
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.data.dto.RecipeStep
import com.ssafy.reper.data.dto.SearchedStore
import com.ssafy.reper.data.dto.StoreResponseUser
import com.ssafy.reper.data.dto.UserToken
import com.ssafy.reper.data.local.SharedPreferencesUtil
import com.ssafy.reper.ui.FcmViewModel
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.boss.BossViewModel
import com.ssafy.reper.ui.boss.NoticeViewModel
import com.ssafy.reper.ui.boss.adpater.NotiAdapter
import com.ssafy.reper.ui.home.adapter.RVHomeLikeRecipeAdapter
import com.ssafy.reper.ui.order.OrderViewModel
import com.ssafy.reper.ui.order.adapter.HomeOrderAdatper
import com.ssafy.reper.ui.recipe.AllRecipeFragment
import com.ssafy.reper.ui.recipe.RecipeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.activity.OnBackPressedCallback
import com.ssafy.reper.base.ApplicationClass
import com.ssafy.reper.base.ApplicationClass.Companion
import com.ssafy.reper.ui.login.LoginActivity
import com.ssafy.reper.util.ViewModelSingleton


private const val TAG = "HomeFragment_싸피"
private const val TAG1 = "HomeFragment_정언"

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var notiAdapter: NotiAdapter
    private lateinit var orderAdapter: HomeOrderAdatper

    private val bannerItems = mutableListOf<HomeBannerModel>()
    private val likeRecipeItems = mutableListOf<HomeLikeRecipeModel>()
    private lateinit var bannerHandler: Handler
    private lateinit var bannerRunnable: Runnable
    private val bossViewModel: BossViewModel by activityViewModels()
    val noticeViewModel: NoticeViewModel by activityViewModels()
    private val storeViewModel: StoreViewModel by activityViewModels()
    private val orderViewModel: OrderViewModel by activityViewModels()
    private val recipeViewModel: RecipeViewModel by viewModels()
    private val mainViewModel: MainActivityViewModel by lazy { ViewModelSingleton.mainActivityViewModel }
    private val fcmViewModel: FcmViewModel by activityViewModels()

    val sharedPreferencesUtil: SharedPreferencesUtil by lazy {
        SharedPreferencesUtil(requireContext().applicationContext)
    }

    private var backPressedTime: Long = 0

    private val orderUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Order update received in HomeFragment")
            when (intent?.action) {
                "com.ssafy.reper.INTERNAL_UPDATE_ORDER" -> {
                    Log.d(TAG, "Refreshing order list in HomeFragment")
                    orderViewModel.getOrders()
                }
            }
        }
    }

    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).showBottomNavigation()

        Log.d(TAG, "onViewCreated: User role = ${sharedPreferencesUtil.getUser()?.role}")
        Log.d(TAG, "onViewCreated: User ID = ${sharedPreferencesUtil.getUser()?.userId}")

        if (sharedPreferencesUtil.getUser()?.role == "OWNER") {
            Log.d(TAG, "onViewCreated: Starting OWNER flow")
            bossViewModel.myStoreList.observe(viewLifecycleOwner) { storeList ->
                Log.d(
                    TAG,
                    "onViewCreated: OWNER stores observer triggered, size=${storeList?.size}"
                )
                initSpinner()
            }
            Log.d(TAG, "onViewCreated: Calling getStoreList...")
            bossViewModel.getStoreList(sharedPreferencesUtil.getUser()?.userId?.toInt() ?: 0)
        } else {
            Log.d(TAG, "onViewCreated: Starting USER flow")
            storeViewModel.myStoreList.observe(viewLifecycleOwner) { storeList ->
                Log.d(TAG, "onViewCreated: USER stores observer triggered, size=${storeList?.size}")
                initSpinner()
            }
            Log.d(TAG, "onViewCreated: Calling getUserStore...")
            storeViewModel.getUserStore(sharedPreferencesUtil.getUser()?.userId?.toInt() ?: 0)
        }

        // 나머지 초기화
        initNotiAdater()
        initOrderAdapter("")
        initFavoriteAdapter()

        // Home Banner 코드!!
        setupBannerItems()
        setupBannerViewPager()

        //스피너 리스트 관찰
        observeStoreListChanges()

        // 공지 더 보러가기 클릭시
        binding.fragmentHomeAnnouncementText.setOnClickListener {

            // BottomNavigationView 숨기기
            (activity as MainActivity).hideBottomNavigation()

            findNavController().navigate(R.id.noticeManageFragment)

        }


        // 레시피 더 보러가기 클릭시
        binding.fragmentHomeLikeRecipeText.setOnClickListener {
            findNavController().popBackStack()
            findNavController().navigate(R.id.allRecipeFragment)

        }

        // 리시버 등록
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                orderUpdateReceiver,
                IntentFilter("com.ssafy.reper.INTERNAL_UPDATE_ORDER")
            )
    }

    private fun initFavoriteAdapter() {
        val rvHomeLikeRecipe = binding.fragmentHomeRvLikeRecipe

        mainViewModel.favoriteRecipeList.observe(viewLifecycleOwner){
            if (mainViewModel.favoriteRecipeList.value.isNullOrEmpty()) {
                // 데이터가 없을 때
                rvHomeLikeRecipe.visibility = View.GONE
                binding.nothingRecipe.visibility = View.VISIBLE
            } else {
                // 데이터가 있을 때
                rvHomeLikeRecipe.visibility = View.VISIBLE
                binding.nothingRecipe.visibility = View.GONE

                val adapter = RVHomeLikeRecipeAdapter(mainViewModel.favoriteRecipeList.value!!.distinctBy { it.recipeName }.toMutableList()) { favoriteRecipe ->
                    mainViewModel.clearData()
                    // 먼저 레시피 데이터를 로드
                    val list = mainViewModel.recipeList.value!!.filter { it.recipeName == favoriteRecipe.recipeName }.toMutableList()
                    Log.d(TAG1, "recipeList: ${list}")
                    val bundle = Bundle().apply {
                        putInt("whereAmICame", 1)
                    }
                    // recipeViewModel의 데이터가 준비되면 mainViewModel 설정
                    mainViewModel.setSelectedRecipes(list)
                    findNavController().navigate(R.id.fullRecipeFragment, bundle)
                }
                rvHomeLikeRecipe.layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                rvHomeLikeRecipe.adapter = adapter
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun initNotiAdater() {
        if (noticeViewModel.noticeList.value.isNullOrEmpty()) {
            noticeViewModel.init(
                sharedPreferencesUtil.getStoreId(),
                sharedPreferencesUtil.getUser().userId!!.toInt()
            )
        }
        binding.fragmentHomeRvAnnouncement.layoutManager = LinearLayoutManager(requireContext())

        noticeViewModel.noticeList.observe(viewLifecycleOwner) { fullList ->
            if (fullList.isNullOrEmpty()) {
                // 데이터가 없을 때
                binding.fragmentHomeRvAnnouncement.visibility = View.GONE
                binding.nothingNotice.visibility = View.VISIBLE
            } else {
                // 데이터가 있을 때
                binding.fragmentHomeRvAnnouncement.visibility = View.VISIBLE
                binding.nothingNotice.visibility = View.GONE

                // 상위 3개만 선택
                val latestNotices = fullList.take(3)
                notiAdapter = NotiAdapter(latestNotices, object : NotiAdapter.ItemClickListener {
                    override fun onClick(position: Int) {
                        val noticeList = noticeViewModel.noticeList.value
                        if (!noticeList.isNullOrEmpty() && position in noticeList.indices) {
                            noticeViewModel.setClickNotice(noticeList[position])
                            findNavController().navigate(R.id.writeNotiFragment)
                        }
                    }
                })
                binding.fragmentHomeRvAnnouncement.adapter = notiAdapter
            }
        }
    }


    fun initSpinner() {
        Log.d(TAG, "initSpinner: Starting spinner initialization")
        val spinner = binding.fragmentHomeStorenameSpinner

        val observeStoreList: (List<Any>) -> Unit = { originalStoreList ->
            Log.d(TAG, "observeStoreList: Received store list, size=${originalStoreList.size}")

            // storeId로 정렬된 리스트 생성
            val storeList = when {
                originalStoreList.isNotEmpty() && originalStoreList[0] is SearchedStore -> {
                    originalStoreList.sortedBy { (it as SearchedStore).storeId }
                }

                originalStoreList.isNotEmpty() && originalStoreList[0] is StoreResponseUser -> {
                    originalStoreList.sortedBy { (it as StoreResponseUser).storeId }
                }

                else -> originalStoreList
            }

            // 기본값으로 "등록된 가게가 없습니다" 설정
            val storeNames = mutableListOf("등록된 가게가 없습니다")
            val storeIds = mutableListOf(0)

            // 가게 목록이 있을 경우에만 기본값을 덮어씀
            if (storeList.isNotEmpty()) {
                when (val firstItem = storeList[0]) {
                    is SearchedStore -> {
                        val filteredList = storeList.mapNotNull {
                            (it as? SearchedStore)?.takeIf { store ->
                                !store.storeName.isNullOrEmpty() && store.storeId != null
                            }
                        }
                        if (filteredList.isNotEmpty()) {
                            storeNames.clear()
                            storeIds.clear()
                            storeNames.addAll(filteredList.map { it.storeName!! })
                            storeIds.addAll(filteredList.map { it.storeId!! })
                        }
                    }

                    is StoreResponseUser -> {
                        val filteredList = storeList.mapNotNull {
                            (it as? StoreResponseUser)?.takeIf { store ->
                                store.name.isNotEmpty()
                            }
                        }
                        if (filteredList.isNotEmpty()) {
                            storeNames.clear()
                            storeIds.clear()
                            storeNames.addAll(filteredList.map { it.name })
                            storeIds.addAll(filteredList.map { it.storeId })
                        }
                    }
                }
            }

            Log.d(TAG, "initSpinner: storeNames=$storeNames, storeIds=$storeIds")

            // Adapter 설정
            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.home_spinner_item,
                storeNames
            ).apply {
                setDropDownViewResource(R.layout.home_spinner_item)
            }

            spinner.adapter = adapter

            // 저장된 storeId 가져오기
            val savedStoreId = sharedPreferencesUtil.getStoreId()
            val defaultIndex = storeIds.indexOfFirst { it == savedStoreId }.takeIf { it != -1 } ?: 0

            // 기본 선택 인덱스 설정
            spinner.setSelection(defaultIndex)
            ApplicationClass.sharedPreferencesUtil.setStoreId(storeIds[defaultIndex])

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedStoreId = storeIds.getOrNull(position) ?: 0
                    sharedPreferencesUtil.setStoreId(selectedStoreId)
                    fcmViewModel.deleteUserToken(sharedPreferencesUtil.getUser().userId!!.toInt())

                    CoroutineScope(Dispatchers.Main).launch {
                        val token = withContext(Dispatchers.IO) {
                            (activity as MainActivity).getFCMToken()
                        }
                        fcmViewModel.saveToken(
                            UserToken(
                                sharedPreferencesUtil.getStoreId(),
                                token,
                                sharedPreferencesUtil.getUser().userId!!.toInt()
                            )
                        )
                        Log.d("FCMTOKEN", token)
                    }


                        noticeViewModel.getAllNotice(
                            selectedStoreId,
                            sharedPreferencesUtil.getUser().userId!!.toInt()
                        )
                        orderViewModel.getOrders()
                        mainViewModel.getLikeRecipes(
                            sharedPreferencesUtil.getStoreId(),
                            sharedPreferencesUtil.getUser().userId!!
                        )
                    Log.d(TAG, "onItemSelected: position=$position, storeId=$selectedStoreId")
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        // OWNER인지 아닌지에 따라 다른 뷰모델을 사용
        if (sharedPreferencesUtil.getUser()?.role == "OWNER") {
            Log.d(TAG, "initSpinner: Observing OWNER stores")
            bossViewModel.myStoreList.value?.let {
                Log.d(TAG, "initSpinner: Current OWNER stores size=${it.size}")
                observeStoreList(it)
            }
        } else {
            Log.d(TAG, "initSpinner: Observing USER stores")
            storeViewModel.myStoreList.value?.let {
                Log.d(TAG, "initSpinner: Current USER stores size=${it.size}")
                observeStoreList(it)
            }
        }
    }

    fun observeStoreListChanges() {
        if (sharedPreferencesUtil.getUser()?.role == "OWNER") {
            bossViewModel.myStoreList.observe(viewLifecycleOwner) { storeList ->
                Log.d(
                    TAG,
                    "observeStoreListChanges: OWNER store list changed, size=${storeList.size}"
                )
                initSpinner()
            }
        } else {
            storeViewModel.myStoreList.observe(viewLifecycleOwner) { storeList ->
                Log.d(
                    TAG,
                    "observeStoreListChanges: USER store list changed, size=${storeList.size}"
                )
                initSpinner()
            }
        }
    }


    fun initOrderAdapter(selectedDate: String) {
        binding.fragmentHomeRvOrder.layoutManager = LinearLayoutManager(requireContext())

        orderViewModel.orderList.observe(viewLifecycleOwner) { orderList ->
            Log.d(TAG, "initOrderAdapter: orderList changed, size=${orderList?.size}")

            mainViewModel.recipeList.value?.let { recipeList ->
                Log.d(TAG, "initOrderAdapter: recipeList size=${recipeList.size}")

                val activeOrders = orderList.filter { order ->
                    !order.completed
                }

                Log.d(TAG, "initOrderAdapter: activeOrders size=${activeOrders.size}")

                if (activeOrders.isEmpty()) {
                    // 활성화된 주문이 없을 때
                    binding.fragmentHomeRvOrder.visibility = View.GONE
                    binding.nothingOrder.visibility = View.VISIBLE
                } else {
                    // 활성화된 주문이 있을 때
                    binding.fragmentHomeRvOrder.visibility = View.VISIBLE
                    binding.nothingOrder.visibility = View.GONE

                    orderAdapter = HomeOrderAdatper(
                        activeOrders.toMutableList(),
                        recipeList.take(activeOrders.size).map { it.recipeName }.toMutableList()
                    ) { orderId ->
                        val bundle = Bundle().apply {
                            putInt("orderId", orderId)
                        }
                        findNavController().navigate(R.id.orderRecipeFragment, bundle)
                    }
                    binding.fragmentHomeRvOrder.adapter = orderAdapter
                }
            }
        }

        // 초기 데이터 로드
        orderViewModel.getOrders()
        Log.d(TAG, "initOrderAdapter: Initial order load requested")
    }


    private fun setupBannerItems() {
        bannerItems.add(
            HomeBannerModel(
                R.drawable.strawberry_banner,
                "딸기 시즌 신메뉴 출시\n외우기 어려운 딸기메뉴\n레퍼가 도와줄게요",
                "딸기메뉴 보러가기",
                R.color.banner_red
            )
        )

        bannerItems.add(
            HomeBannerModel(
                R.drawable.banner3,
                "카페 근무는\n처음이신가요?\n레퍼와 함께!",
                "사용법 보러가기",
                R.color.banner_green
            )
        )

        bannerItems.add(
            HomeBannerModel(
                R.drawable.storebanner,
                "여러 가게 정보도\n한번에 관리\n레퍼에선 모두 됩니다!",
                "마이페이지 보러가기",
                R.color.banner_blue
            )
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupBannerViewPager() {
        val bannerAdapter = RVHomeBannerAdapter(bannerItems) { selectedItem ->
            when (selectedItem.imageUrl) {
                R.drawable.strawberry_banner -> {
                    val bundle = Bundle().apply {
                        putString("searchQuery", "딸기") //검색어 전달
                    }
                    findNavController().popBackStack()
                    findNavController().navigate(R.id.allRecipeFragment, bundle)

                }

                R.drawable.banner3 -> {
                    //노션이나 튜토리얼 액티비티
                }

                R.drawable.storebanner -> {
                    findNavController().popBackStack()
                    findNavController().navigate(R.id.myPageFragment)
                }
            }


        }
        binding.fragmentHomeVpBanner.apply {
            adapter = bannerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL

            // 무한 스크롤을 위해 시작 위치를 최대값의 중간으로 설정
            // 이렇게 하면 왼쪽이나 오른쪽으로 충분히 스크롤할 수 있음
            val startPosition = Int.MAX_VALUE / 2 - (Int.MAX_VALUE / 2) % bannerItems.size
            setCurrentItem(startPosition, false)

            // ViewPager의 스크롤 상태 변경을 감지하는 리스너
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    when (state) {
                        // 스크롤이 멈췄을 때 자동 스크롤 재시작
                        ViewPager2.SCROLL_STATE_IDLE -> startBannerAutoScroll()
                        // 사용자가 드래그 시작할 때 자동 스크롤 중지
                        ViewPager2.SCROLL_STATE_DRAGGING -> stopBannerAutoScroll()
                    }
                }
            })

            // ViewPager2의 내부 RecyclerView에 터치 이벤트 리스너 추가
            getChildAt(0).setOnTouchListener { _, event ->
                when (event.action) {
                    // 화면을 터치했을 때 자동 스크롤 중지
                    android.view.MotionEvent.ACTION_DOWN -> stopBannerAutoScroll()
                    // 터치가 끝났거나 취소됐을 때 자동 스크롤 재시작
                    android.view.MotionEvent.ACTION_UP,
                    android.view.MotionEvent.ACTION_CANCEL -> startBannerAutoScroll()
                }
                // false를 반환하여 터치 이벤트가 상위 뷰로 전파되도록 함
                false
            }
        }

        // 자동 스크롤을 위한 Handler와 Runnable 설정
        bannerHandler = Handler(Looper.getMainLooper())
        bannerRunnable = Runnable {
            // 현재 위치에서 다음 아이템으로 이동
            binding.fragmentHomeVpBanner.currentItem = binding.fragmentHomeVpBanner.currentItem + 1
        }

        // 초기 자동 스크롤 시작
        startBannerAutoScroll()
    }

    // 자동 스크롤을 시작하는 함수
    private fun startBannerAutoScroll() {
        // 이전에 예약된 스크롤이 있다면 제거
        bannerHandler.removeCallbacks(bannerRunnable)
        // 6초 후에 다음 페이지로 넘어가도록 예약
        bannerHandler.postDelayed(bannerRunnable, 6000)
    }

    // 자동 스크롤을 중지하는 함수
    private fun stopBannerAutoScroll() {
        // 예약된 스크롤 작업을 모두 제거
        bannerHandler.removeCallbacks(bannerRunnable)
    }


    // 화면이 다시 보일 때 자동 스크롤 재시작
    override fun onResume() {
        super.onResume()

        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // 화면 회전 잠금

        // 자동 배너 스크롤 시작
        startBannerAutoScroll()

        // 뒤로 가기 버튼 처리
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - backPressedTime <= 2000) {
                        requireActivity().finish()
                    } else {
                        backPressedTime = currentTime
                        Toast.makeText(
                            requireContext(),
                            "뒤로 가기 버튼을 한 번 더 누르면 종료됩니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        )
    }


    // 화면이 가려질 때 자동 스크롤 중지
    override fun onPause() {
        super.onPause()
        stopBannerAutoScroll()
    }

    // 뷰가 파괴될 때 자동 스크롤 중지 및 메모리 정리
    override fun onDestroyView() {
        super.onDestroyView()
        stopBannerAutoScroll()
        // 리시버 해제
        try {
            LocalBroadcastManager.getInstance(requireContext())
                .unregisterReceiver(orderUpdateReceiver)
        } catch (e: Exception) {
            Log.e(TAG, "Error unregistering receiver: ${e.message}")
        }
        _binding = null
    }

}