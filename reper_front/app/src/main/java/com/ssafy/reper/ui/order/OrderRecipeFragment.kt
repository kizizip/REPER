package com.ssafy.reper.ui.order

import MainActivityViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ssafy.reper.R
import com.ssafy.reper.base.ApplicationClass
import com.ssafy.reper.data.dto.Order
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.databinding.FragmentOrderRecipeBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.order.adapter.OrderRecipeAdatper
import com.ssafy.reper.util.ViewModelSingleton

private const val TAG = "OrderRecipeFragment_정언"
class OrderRecipeFragment : Fragment() {

    lateinit var order:Order
    // check 표시된 레시피 저장 리스트
    var checkedRecipeList:MutableList<Recipe> = mutableListOf()
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
                checkedRecipeList.clear()
                checkedRecipeList.addAll(viewModel.recipeList.value ?: mutableListOf())
            }
            else{
                checkedRecipeList.clear()
            }
            Log.d(TAG, "initEvent: ${checkedRecipeList}")
            orderRecipeAdapter.checkedRecipeList = checkedRecipeList.toMutableList()
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

            // order를 이미 완료 처리한 상태라면, 완료 버튼 안보이게!
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

        // 레시피 보기 버튼 클릭
        orderRecipebinding.orderRecipeFragmentGoRecipeBtn.setOnClickListener {
            mainViewModel.clearData()
            mainViewModel.setOrder(order)
            mainViewModel.getLikeRecipes(ApplicationClass.sharedPreferencesUtil.getStoreId(), ApplicationClass.sharedPreferencesUtil.getUser().userId!!)

            if(orderRecipebinding.orderRecipeFragmentAllRecipeTab.isSelected == true){
                navigateToRecipeFragment(R.id.fullRecipeFragment)
            }
            else if(orderRecipebinding.orderRecipeFragmentStepbystepRecipeTab.isSelected == true){
                navigateToRecipeFragment(R.id.stepRecipeFragment)
            }
        }
    }
    // 어뎁터 설정
    fun initAdapter() {
        orderRecipeAdapter = OrderRecipeAdatper(mutableListOf(), mutableListOf(), checkedRecipeList) { recipe, isChecked ->
            // 클릭 이벤트 -> recipeId 저장, 삭제
            if(isChecked){
                checkedRecipeList.add(recipe)
            }
            else{
                checkedRecipeList.remove(recipe)
            }

            if(checkedRecipeList.count() == viewModel.recipeList.value!!.count()){
                orderRecipebinding.orderrecipeFmCheckbox.isChecked = isChecked
            }
            else {
                orderRecipebinding.orderrecipeFmCheckbox.isChecked = false
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
    fun navigateToRecipeFragment(fragmentId :Int) {
        val bundle =Bundle().apply {
            putInt("whereAmICame", 2)
        }
        mainViewModel.setOrderRecipeList(viewModel.recipeList.value!!)
        mainViewModel.setSelectedRecipes(checkedRecipeList)
        findNavController().navigate(fragmentId, bundle)
    }
}