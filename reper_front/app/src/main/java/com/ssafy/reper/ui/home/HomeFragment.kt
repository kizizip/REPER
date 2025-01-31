package com.ssafy.reper.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.reper.R
import com.ssafy.reper.data.local.HomeAnnouncementModel
import com.ssafy.reper.data.local.HomeBannerModel
import com.ssafy.reper.data.local.HomeLikeRecipeModel
import com.ssafy.reper.data.local.HomeOrderModel
import com.ssafy.reper.databinding.FragmentHomeBinding
import com.ssafy.reper.ui.home.adapter.RVHomeAnnouncement
import com.ssafy.reper.ui.home.adapter.RVHomeBannerAdapter
import com.ssafy.reper.ui.home.adapter.RVHomeLikeRecipeAdapter
import com.ssafy.reper.ui.home.adapter.RVHomeOrderAdapter
import android.os.Handler
import android.os.Looper
import androidx.viewpager2.widget.ViewPager2
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.boss.NoticeManageFragment
import com.ssafy.reper.ui.order.OrderRecipeFragment
import com.ssafy.reper.ui.recipe.AllRecipeFragment

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val bannerItems = mutableListOf<HomeBannerModel>()
    private val announcementItems = mutableListOf<HomeAnnouncementModel>()
    private val likeRecipeItems = mutableListOf<HomeLikeRecipeModel>()
    private val orderItems = mutableListOf<HomeOrderModel>()
    private lateinit var bannerHandler: Handler
    private lateinit var bannerRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // 가게 이름 Spinner 설정
        val spinner = binding.fragmentHomeStorenameSpinner
        val userTypes = arrayOf("메가커피 구미 인동점", "이스터에그:이걸 발견하다니!")

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.home_spinner_item,
            userTypes
        ).apply {
            setDropDownViewResource(R.layout.home_spinner_item)
        }

        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = userTypes[position]
                // 선택된 항목 처리
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무것도 선택되지 않았을 때의 처리
            }
        }



        // Home Banner 코드!!
        setupBannerItems()
        setupBannerViewPager()


        // HomeAnnouncement(공지사항) 코드!!!
        announcementItems.add(
            HomeAnnouncementModel(
                "🔥내용🔥 멋쟁이 우리팀들 화이팅!!1",
                "1분전"
            )
        )

        announcementItems.add(
            HomeAnnouncementModel(
                "🔥내용🔥 멋쟁이 우리팀들 화이팅!!2",
                "2분전"
            )
        )

        announcementItems.add(
            HomeAnnouncementModel(
                "🔥내용🔥 멋쟁이 우리팀들 화이팅!!3",
                "3분전"
            )
        )

        val rvHomeAnnouncement = binding.fragmentHomeRvAnnouncement
        val rvHomeAnnouncementAdapter = RVHomeAnnouncement(announcementItems)

        rvHomeAnnouncement.adapter = rvHomeAnnouncementAdapter
        rvHomeAnnouncement.layoutManager = LinearLayoutManager(context,  LinearLayoutManager.VERTICAL, false)


        // 공지 더 보러가기 클릭시
        binding.fragmentHomeAnnouncementText.setOnClickListener {

            // BottomNavigationView 숨기기
            (activity as MainActivity).hideBottomNavigation()

            // NoticeManageFragment로 이동
            parentFragmentManager.beginTransaction()
                .replace(R.id.activityMainFragmentContainer, NoticeManageFragment())
                .addToBackStack(null)
                .commit()

        }


        // HomeLikeRecipe(즐겨찾는 레시피) 코드!!!
        likeRecipeItems.add(
            HomeLikeRecipeModel(
                "아메리카노(HOT)1",
                R.drawable.americano_hot
            )
        )

        likeRecipeItems.add(
            HomeLikeRecipeModel(
                "아메리카노(HOT)2",
                R.drawable.americano_hot
            )
        )

        likeRecipeItems.add(
            HomeLikeRecipeModel(
                "아메리카노(HOT)3",
                R.drawable.americano_hot
            )
        )

        likeRecipeItems.add(
            HomeLikeRecipeModel(
                "아메리카노(HOT)4",
                R.drawable.americano_hot
            )
        )

        likeRecipeItems.add(
            HomeLikeRecipeModel(
                "아메리카노(HOT)5",
                R.drawable.americano_hot
            )
        )

        val rvHomeLikeRecipe = binding.fragmentHomeRvLikeRecipe
        val rvHomeLikeRecipeAdapter = RVHomeLikeRecipeAdapter(likeRecipeItems)

        rvHomeLikeRecipe.adapter = rvHomeLikeRecipeAdapter
        rvHomeLikeRecipe.layoutManager = LinearLayoutManager(context,  LinearLayoutManager.HORIZONTAL, false)


        // 레시피 더 보러가기 클릭시
        binding.fragmentHomeLikeRecipeText.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.activityMainFragmentContainer, AllRecipeFragment())
                .commit()

            (activity as MainActivity).getBottomNavigationView().selectedItemId = R.id.recipe_icon
        }


        // 현재 진행중인 주문 코드!!!
        orderItems.add(
            HomeOrderModel(
                "아메리카노 외.. 1잔",
                "1분전"
            )
        )

        orderItems.add(
            HomeOrderModel(
                "아메리카노 외.. 2잔",
                "2분전"
            )
        )

        orderItems.add(
            HomeOrderModel(
                "아메리카노 외.. 3잔",
                "3분전"
            )
        )

        val rvHomeOrder = binding.fragmentHomeRvOrder
        val rvHomeOrderAdapter = RVHomeOrderAdapter(orderItems)

        rvHomeOrder.adapter = rvHomeOrderAdapter
        rvHomeOrder.layoutManager = LinearLayoutManager(context,  LinearLayoutManager.VERTICAL, false)

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
                R.drawable.christmas_banner,
                "만들기 어려운\n크리스마스 신메뉴도\n레퍼와 함께!",
                "사용법 보러가기기",
                R.color.banner_green
            )
        )

        bannerItems.add(
            HomeBannerModel(
                R.drawable.christmas_banner,
                "테스트 배너",
                "테스트 버튼",
                R.color.banner_green
            )
        )
    }

    private fun setupBannerViewPager() {
        val bannerAdapter = RVHomeBannerAdapter(bannerItems)
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
        startBannerAutoScroll()
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
        _binding = null
    }

}