package com.ssafy.reper.ui.order

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.reper.R
import com.ssafy.reper.data.local.HomeOrderModel
import com.ssafy.reper.databinding.FragmentHomeBinding
import com.ssafy.reper.databinding.FragmentOrderBinding
import com.ssafy.reper.ui.home.adapter.RVHomeOrderAdapter
import com.ssafy.reper.ui.MainActivity

class OrderFragment : Fragment() {

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!

    // HomeOrderModel 재사용
    private val orderItems = mutableListOf<HomeOrderModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // 주문 날짜 Spinner 설정
        val dateSpinner = binding.fragmentOrderDateSpinner
        val dates = arrayOf("2025.01.20", "2025.01.19", "2025.01.18")

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.order_spinner_item,
            dates
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        dateSpinner.adapter = adapter

        dateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = dates[position]
                // 선택된 항목 처리
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무것도 선택되지 않았을 때의 처리
            }
        }


        // 주문내역 코드!!!
        orderItems.add(
            HomeOrderModel(
                "아메리카노 외.. 1잔",
                "1분전"
            )
        )

        orderItems.add(
            HomeOrderModel(
                "카페모카 외.. 2잔",
                "2분전"
            )
        )

        orderItems.add(
            HomeOrderModel(
                "아메리카노 외.. 3잔",
                "3분전"
            )
        )

        orderItems.add(
            HomeOrderModel(
                "아메리카노 외.. 3잔",
                "3분전"
            )
        )

        orderItems.add(
            HomeOrderModel(
                "아메리카노 외.. 4잔",
                "완료"
            )
        )

        orderItems.add(
            HomeOrderModel(
                "아메리카노 외.. 2잔",
                "완료"
            )
        )

        orderItems.add(
            HomeOrderModel(
                "아메리카노 외.. 1잔",
                "완료"
            )
        )
        orderItems.add(
            HomeOrderModel(
                "아메리카노 외.. 1잔",
                "완료"
            )
        )
        orderItems.add(
            HomeOrderModel(
                "아메리카노 외.. 1잔",
                "완료"
            )
        )
        orderItems.add(
            HomeOrderModel(
                "아메리카노 외.. 1잔",
                "완료"
            )
        )
        orderItems.add(
            HomeOrderModel(
                "아메리카노 외.. 1잔",
                "완료"
            )
        )

        // RVHomeOrderAdapter 재사용
        val rvOrder = binding.fragmentOrderRvOrder
        val rvOrderAdapter = RVHomeOrderAdapter(orderItems)

        rvOrder.adapter = rvOrderAdapter
        rvOrder.layoutManager = LinearLayoutManager(context,  LinearLayoutManager.VERTICAL, false)

        rvOrderAdapter.itemClick = object : RVHomeOrderAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                // BottomNavigationView 숨기기
                (activity as MainActivity).hideBottomNavigation()
                
                // OrderRecipeFragment로 이동
                parentFragmentManager.beginTransaction()
                    .replace(R.id.activityMainFragmentContainer, OrderRecipeFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}