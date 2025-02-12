package com.ssafy.reper.ui.order

import MainActivityViewModel
import android.app.Dialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ssafy.reper.R
import com.ssafy.reper.base.ApplicationClass
import com.ssafy.reper.data.dto.Order
import com.ssafy.reper.data.local.OrderRecipeModel
import com.ssafy.reper.databinding.FragmentOrderRecipeBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.order.adapter.OrderAdatper
import com.ssafy.reper.ui.order.adapter.OrderRecipeAdatper
import com.ssafy.reper.util.ViewModelSingleton
import kotlin.math.log

private const val TAG = "OrderRecipeFragment_정언"
class OrderRecipeFragment : Fragment() {

    lateinit var order:Order
    // check 표시된 레시피 ID 저장 리스트
    var checkedRecipeIdList:MutableList<Int> = mutableListOf()
    // OrderFragment에서 날라오는 orderId
    var orderId = -1

    private val mainViewModel: MainActivityViewModel by lazy { ViewModelSingleton.mainActivityViewModel }
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

        // 어뎁터 처리
        initAdapter()
        // 이벤트 처리
        initEvent()
    }
    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNavigation()
    }
    override fun onDestroy() {
        super.onDestroy()
        _orderRecipebinding = null
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
                checkedRecipeIdList.clear()
                for(detail in  orderRecipeAdapter.orderDetailList){
                    checkedRecipeIdList.add(detail.recipeId)
                }
            }
            else{
                checkedRecipeIdList.clear()
            }
            orderRecipeAdapter.checkedRecipeIdList = checkedRecipeIdList
            orderRecipeAdapter.notifyDataSetChanged()
        }

        orderRecipebinding.orderRecipeFragmentCompleteOrderBtn.setOnClickListener {
            viewModel.completeOrder(orderId)
            parentFragmentManager.popBackStack()
        }

        viewModel.order.observe(viewLifecycleOwner){
            order = it
            orderRecipebinding.fragmentOrderRecipeTvTakeout.let{text ->
                if(it.takeout){
                    text.setText("포장")
                    text.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                }
                else{
                    text.setText("매장")
                    text.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                }
            }

            orderRecipebinding.orderRecipeFragmentCompleteOrderBtn.let {
                if(order.completed){
                    it.visibility = View.GONE
                }
                else{
                    it.visibility = View.VISIBLE
                }
            }
            orderRecipebinding.orderRecipeFragmentGoRecipeBtn.let {
                if(order.completed){
                    it.setBackgroundResource(R.drawable.btn)
                }
                else{
                    it.setBackgroundResource(R.drawable.medium_green_button)
                }
            }
        }

        orderRecipebinding.orderRecipeFragmentGoRecipeBtn.setOnClickListener {
            mainViewModel.setOrder(order)
            mainViewModel.getSelectedRecipes(checkedRecipeIdList)
            mainViewModel.selectedRecipeList.observe(viewLifecycleOwner){
                Log.d(TAG, "selectedOrder: ${it}")
                val bundle = Bundle().apply {
                    putInt("whereAmICame", 2)
                }

                mainViewModel.setNowISeeRecipe(0)
                mainViewModel.setNowISeeStep(-1)

                if(orderRecipebinding.orderRecipeFragmentAllRecipeTab.isSelected == true){
                    findNavController().navigate(R.id.allRecipeFragment, bundle)
                }
                else if(orderRecipebinding.orderRecipeFragmentStepbystepRecipeTab.isSelected == true){
                    findNavController().navigate(R.id.stepRecipeFragment, bundle)
                }
            }
        }
    }
    // 어뎁터 설정
    fun initAdapter() {
        orderRecipeAdapter = OrderRecipeAdatper(mutableListOf(), mutableListOf(), checkedRecipeIdList) { recipeId, isChecked ->
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