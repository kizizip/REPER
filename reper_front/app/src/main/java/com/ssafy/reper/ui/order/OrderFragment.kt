package com.ssafy.reper.ui.order

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ssafy.reper.R
import com.ssafy.reper.databinding.FragmentOrderBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.order.adapter.OrderAdatper

private const val TAG = "OrderFragment_정언"
class OrderFragment : Fragment() {
    // spinner 클릭한 날짜
    var selectedDate:String = ""
    // 주문 날짜 모음 yyyy-MM-dd 형식
    var orderDateList: MutableList<String> = mutableListOf()

    private val viewModel: OrderViewModel by viewModels()

    // 주문 리스트 recyclerView Adapter
    private lateinit var orderAdapter : OrderAdatper

    private lateinit var mainActivity: MainActivity
    private var _orderBinding: FragmentOrderBinding? = null
    private val orderBinding get() = _orderBinding!!

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
        _orderBinding = FragmentOrderBinding.inflate(inflater, container, false)
        return orderBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //  초기화
        resetData()
        //어뎁터설정
        initAdapter("")
    }
    override fun onResume() {
        super.onResume()
        mainActivity.showBottomNavigation()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _orderBinding = null
    }
    override fun onDetach() {
        super.onDetach()
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED // 화면 회전 잠금 해제
    }

    // 데이터 초기화
    fun resetData(){
        selectedDate = ""
        orderDateList = mutableListOf()
    }
    // datespinner 설정
    fun configureDateSpinner(){
        val dateSpinner = orderBinding.fragmentOrderDateSpinner
        val adapter = ArrayAdapter(requireContext(), R.layout.order_spinner_item, orderDateList).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        dateSpinner.adapter = adapter

        dateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(!selectedDate.equals(orderDateList[position])){
                    selectedDate = orderDateList[position]
                    initAdapter(selectedDate)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무것도 선택되지 않았을 때의 처리
            }
        }
    }
    // 어뎁터 설정
    fun initAdapter(selectedDate: String) {
        orderAdapter = OrderAdatper(mutableListOf(), mutableListOf()) { orderId ->
            // 클릭 이벤트 -> orderId 전달
            val bundle = Bundle().apply {
                putInt("orderId", orderId)
            }
            findNavController().navigate(R.id.orderRecipeFragment, bundle)
        }

        // 데이터 저장
        orderBinding.fragmentOrderRvOrder.apply {
            viewModel.getOrders()
            viewModel.orderList.observe(viewLifecycleOwner) { orderList ->
                orderAdapter.orderList.clear()
                orderAdapter.recipeNameList.clear()

                for (item in orderList) {
                    if (!orderDateList.contains(item.orderDate.substring(0, 10))) {
                        orderDateList.add(item.orderDate.substring(0, 10))
                        orderDateList.sortedDescending()
                        configureDateSpinner()
                    }
                    if (selectedDate.isNotBlank() && item.orderDate.substring(0, 10) == selectedDate) {
                        if(!orderAdapter.orderList.contains(item)){
                            orderAdapter.orderList.add(item)
                            orderAdapter.recipeNameList.add(viewModel.recipeNameList.value?.get(orderList.indexOf(item))?.recipeName!!)
                        }
                    }
                }
                adapter = orderAdapter
            }
        }
    }
}
