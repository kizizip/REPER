package com.ssafy.reper.ui.order

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ssafy.reper.R
import com.ssafy.reper.data.local.OrderRecipeModel
import com.ssafy.reper.databinding.FragmentOrderRecipeBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.order.adapter.OrderAdatper
import com.ssafy.reper.ui.order.adapter.OrderRecipeAdatper
import kotlin.math.log

private const val TAG = "OrderRecipeFragment_정언"
class OrderRecipeFragment : Fragment() {

    // check 표시된 레시피 ID 저장 리스트
    var checkedRecipeIdList:MutableList<Int> = mutableListOf()
    // OrderFragment에서 날라오는 orderId
    var orderId = -1

    private val viewModel: OrderViewModel by viewModels()

    // 주문 레시피 리스트 recyclerView Adapter
    private lateinit var orderRecipeAdapter : OrderRecipeAdatper

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
        orderId = arguments?.getInt("orderId") ?: -1

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

        // 단계별 레시피 탭
        orderRecipebinding.orderRecipeFragmentStepbystepRecipeTab.setOnClickListener {
            orderRecipebinding.orderRecipeFragmentStepbystepRecipeTab.isSelected = true
            orderRecipebinding.orderRecipeFragmentAllRecipeTab.isSelected = false
        }

        // 전체 레시피 탭
        orderRecipebinding.orderRecipeFragmentAllRecipeTab.setOnClickListener {
            orderRecipebinding.orderRecipeFragmentStepbystepRecipeTab.isSelected = false
            orderRecipebinding.orderRecipeFragmentAllRecipeTab.isSelected = true
        }

        // 전체선택
        orderRecipebinding.orderrecipeFmCheckbox.setOnClickListener{
            if(orderRecipebinding.orderrecipeFmCheckbox.isChecked){
                orderRecipeAdapter.checkedAll = true
                orderRecipeAdapter.notifyDataSetChanged()
                checkedRecipeIdList.clear()
                for(detail in  orderRecipeAdapter.orderDetailList){
                    checkedRecipeIdList.add(detail.recipeId)
                }
            }
            else{
                orderRecipeAdapter.checkedAll = false
                orderRecipeAdapter.notifyDataSetChanged()
                checkedRecipeIdList.clear()
            }
        }

        orderRecipebinding.orderRecipeFragmentCompleteOrderBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
    // 어뎁터 설정
    fun initAdapter() {
        orderRecipeAdapter = OrderRecipeAdatper(mutableListOf(), mutableListOf(), false) { recipeId, isChecked ->
            // 클릭 이벤트 -> recipeId 저장, 삭제
            if(isChecked){
                checkedRecipeIdList.add(recipeId)
                if(checkedRecipeIdList.count() == orderRecipeAdapter.recipeList.count()){
                    orderRecipebinding.orderrecipeFmCheckbox.isChecked = true
                }
            }
            else{
                checkedRecipeIdList.remove(recipeId)
                if(checkedRecipeIdList.count() != orderRecipeAdapter.recipeList.count()){
                    orderRecipebinding.orderrecipeFmCheckbox.isChecked = false
                }
            }
        }

        // 데이터 저장
        orderRecipebinding.fragmentOrderRecipeRv.apply {
            viewModel.getOrder(orderId)

            viewModel.recipeList.observe(viewLifecycleOwner){
                orderRecipeAdapter.orderDetailList = viewModel.order.value!!.orderDetails
                orderRecipeAdapter.recipeList = it
                adapter = orderRecipeAdapter
            }
        }
    }
}