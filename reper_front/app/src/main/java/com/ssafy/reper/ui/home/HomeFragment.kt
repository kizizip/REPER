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

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val bannerItems = mutableListOf<HomeBannerModel>()
    private val announcementItems = mutableListOf<HomeAnnouncementModel>()
    private val likeRecipeItems = mutableListOf<HomeLikeRecipeModel>()
    private val orderItems = mutableListOf<HomeOrderModel>()



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

        val rvHomeBanner = binding.fragmentHomeRvHomBanner
        val rvHomeBannerAdapter = RVHomeBannerAdapter(bannerItems)

        rvHomeBanner.adapter = rvHomeBannerAdapter
        rvHomeBanner.layoutManager = LinearLayoutManager(context,  LinearLayoutManager.HORIZONTAL, false)


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
            Toast.makeText(context, "공지사항으로 ㄱㄱ", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(context, "즐겨찾는레시피로 ㄱㄱ", Toast.LENGTH_SHORT).show()
        }


        // 현재 진행중인 주문 코드!!!
        orderItems.add(
            HomeOrderModel(
                "아메리카노 외 1잔",
                "1분전"
            )
        )

        orderItems.add(
            HomeOrderModel(
                "아메리카노 외 2잔",
                "2분전"
            )
        )

        orderItems.add(
            HomeOrderModel(
                "아메리카노 외 3잔",
                "3분전"
            )
        )

        val rvHomeOrder = binding.fragmentHomeRvOrder
        val rvHomeOrderAdapter = RVHomeOrderAdapter(orderItems)

        rvHomeOrder.adapter = rvHomeOrderAdapter
        rvHomeOrder.layoutManager = LinearLayoutManager(context,  LinearLayoutManager.VERTICAL, false)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}