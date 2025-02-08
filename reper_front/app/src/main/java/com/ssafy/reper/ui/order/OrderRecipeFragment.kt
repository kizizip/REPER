package com.ssafy.reper.ui.order

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.reper.R
import com.ssafy.reper.data.local.OrderRecipeModel
import com.ssafy.reper.databinding.FragmentOrderRecipeBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.order.adapter.OrderAdatper


class OrderRecipeFragment : Fragment() {

    private val orderdetailItems = mutableListOf<OrderRecipeModel>()

    private lateinit var mainActivity: MainActivity
    private var _orderRecipebinding: FragmentOrderRecipeBinding? = null
    private val orderRecipebinding get() = _orderRecipebinding!!

    // 안드로이드 라이프 사이클
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // 화면 회전 잠금
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _orderRecipebinding = FragmentOrderRecipeBinding.inflate(inflater, container, false)
        return orderRecipebinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // OrderFragment에서 bundle로 던진 orderId를 받음
        val orderId = arguments?.getInt("orderId")

        // 이벤트 처리
        initEvent()
        // 어뎁터 처리
        initAdapter()
    }
    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNavigation()
    }
    override fun onDestroy() {
        super.onDestroy()
        // Fragment가 파괴될 때 BottomNavigationView 다시 보이게 하기
        (activity as MainActivity).showBottomNavigation()
        _orderRecipebinding = null
    }
    override fun onDetach() {
        super.onDetach()
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED // 화면 회전 잠금 해제
    }

    // 이벤트 처리
    fun initEvent(){
        // 뒤로가기 버튼 클릭 리스너 설정
        orderRecipebinding.notiFgBackIcon.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 탭 초기 상태 설정 (단계별 레시피 선택)
        orderRecipebinding.orderRecipeFragmentStepbystepRecipeTab.isSelected = true
        orderRecipebinding.orderRecipeFragmentAllRecipeTab.isSelected = false

        // 탭 클릭 리스너 설정
        orderRecipebinding.orderRecipeFragmentStepbystepRecipeTab.setOnClickListener {
            orderRecipebinding.orderRecipeFragmentStepbystepRecipeTab.isSelected = true
            orderRecipebinding.orderRecipeFragmentAllRecipeTab.isSelected = false
            // 여기에 단계별 레시피 표시 로직 추가
        }

        orderRecipebinding.orderRecipeFragmentAllRecipeTab.setOnClickListener {
            orderRecipebinding.orderRecipeFragmentStepbystepRecipeTab.isSelected = false
            orderRecipebinding.orderRecipeFragmentAllRecipeTab.isSelected = true
            // 여기에 전체 레시피 표시 로직 추가
        }
    }
    // 어뎁터 설정
    fun initAdapter() {
//        orderAdapter = OrderAdatper(mutableListOf(), mutableListOf()) { orderId ->
//            // 클릭 이벤트 -> orderId 전달
//            val bundle = Bundle().apply {
//                putInt("orderId", orderId)
//            }
//            findNavController().navigate(R.id.orderRecipeFragment, bundle)
//        }
//
//        // 데이터 저장
//        orderBinding.fragmentOrderRvOrder.apply {
//            viewModel.getOrders()
//            viewModel.orderList.observe(viewLifecycleOwner) { orderList ->
//                Log.d(TAG, "initAdapter: ${orderList}")
//                orderAdapter.orderList.clear()
//                orderAdapter.recipeNameList.clear()
//
//                for (item in orderList) {
//                    if (!orderDateList.contains(item.orderDate.substring(0, 10))) {
//                        orderDateList.add(item.orderDate.substring(0, 10))
//                        orderDateList.sortedDescending()
//                        configureDateSpinner()
//                    }
//                    if (selectedDate.isNotBlank() && item.orderDate.substring(0, 10) == selectedDate) {
//                        if(!orderAdapter.orderList.contains(item)){
//                            orderAdapter.orderList.add(item)
//                            orderAdapter.recipeNameList.add(viewModel.recipeNameList.value?.get(orderList.indexOf(item))?.recipeName!!)
//                        }
//                    }
//                }
//                Log.d(TAG, "dateList : ${orderDateList}")
//                Log.d(TAG, "orderList : ${orderAdapter.orderList}")
//                Log.d(TAG, "recipeNameList : ${orderAdapter.recipeNameList}")
//                adapter = orderAdapter
//            }
//        }
    }
}