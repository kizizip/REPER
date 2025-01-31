package com.ssafy.reper.ui.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.reper.R
import com.ssafy.reper.data.local.HomeAnnouncementModel
import com.ssafy.reper.data.local.HomeBannerModel
import com.ssafy.reper.data.local.OrderRecipeModel
import com.ssafy.reper.databinding.FragmentOrderBinding
import com.ssafy.reper.databinding.FragmentOrderRecipeBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.home.adapter.RVHomeAnnouncement
import com.ssafy.reper.ui.order.adapter.RVOrderRecipeAdapter


class OrderRecipeFragment : Fragment() {

    private var _binding: FragmentOrderRecipeBinding? = null
    private val binding get() = _binding!!

    private val orderdetailItems = mutableListOf<OrderRecipeModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentOrderRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 탭 초기 상태 설정 (단계별 레시피 선택)
        binding.orderRecipeFragmentStepbystepRecipeTab.isSelected = true
        binding.orderRecipeFragmentAllRecipeTab.isSelected = false

        // 탭 클릭 리스너 설정
        binding.orderRecipeFragmentStepbystepRecipeTab.setOnClickListener {
            binding.orderRecipeFragmentStepbystepRecipeTab.isSelected = true
            binding.orderRecipeFragmentAllRecipeTab.isSelected = false
            // 여기에 단계별 레시피 표시 로직 추가
        }

        binding.orderRecipeFragmentAllRecipeTab.setOnClickListener {
            binding.orderRecipeFragmentStepbystepRecipeTab.isSelected = false
            binding.orderRecipeFragmentAllRecipeTab.isSelected = true
            // 여기에 전체 레시피 표시 로직 추가
        }

        // 주문 상세 리스트 코드!!!
        orderdetailItems.add(
            OrderRecipeModel(
                R.drawable.americano_hot,
                "아메리카노 hot",
                2,
                "없음"
            )
        )

        orderdetailItems.add(
            OrderRecipeModel(
                R.drawable.americano_hot,
                "아메리카노 Ice",
                3,
                "샷 추가"
            )
        )

        orderdetailItems.add(
            OrderRecipeModel(
                R.drawable.americano_hot,
                "카페모카 Ice",
                1,
                "휘핑 빼고"
            )
        )

        orderdetailItems.add(
            OrderRecipeModel(
                R.drawable.americano_hot,
                "카페라떼 hot",
                2,
                "없음"
            )
        )

        orderdetailItems.add(
            OrderRecipeModel(
                R.drawable.americano_hot,
                "아메리카노 hot",
                2,
                "없음"
            )
        )

        orderdetailItems.add(
            OrderRecipeModel(
                R.drawable.americano_hot,
                "아메리카노 hot",
                2,
                "없음"
            )
        )

        val rvOrderRecipe = binding.fragmentOrderRecipeRv
        val rvOrderRecipeAdapter = RVOrderRecipeAdapter(orderdetailItems)

        rvOrderRecipe.adapter = rvOrderRecipeAdapter
        rvOrderRecipe.layoutManager = LinearLayoutManager(context,  LinearLayoutManager.VERTICAL, false)

    }



    override fun onDestroy() {
        super.onDestroy()
        // Fragment가 파괴될 때 BottomNavigationView 다시 보이게 하기
        (activity as MainActivity).showBottomNavigation()

        _binding = null
    }

}