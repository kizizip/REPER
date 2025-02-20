package com.ssafy.reper.ui.order

import MainActivityViewModel
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LayoutAnimationController
import android.view.animation.TranslateAnimation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.ViewCompat.animate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ssafy.reper.R
import com.ssafy.reper.base.ApplicationClass
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.databinding.FragmentOrderBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.order.adapter.OrderAdatper
import com.ssafy.reper.util.ViewModelSingleton
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

private const val TAG = "OrderFragment_정언"
class OrderFragment : Fragment() {
    // spinner 클릭한 날짜
    var selectedDate:String = ""
    // 주문 날짜 모음 yyyy-MM-dd 형식
    var orderDateList: MutableList<String> = mutableListOf()

    var recipeList :MutableList<Recipe> = mutableListOf()

    private val mainViewModel: MainActivityViewModel by lazy { ViewModelSingleton.mainActivityViewModel }
    private val viewModel: OrderViewModel by activityViewModels()

    // 주문 리스트 recyclerView Adapter
    private lateinit var orderAdapter : OrderAdatper

    private lateinit var mainActivity: MainActivity
    private var _orderBinding: FragmentOrderBinding? = null
    private val orderBinding get() = _orderBinding!!

    // 안드로이드 라이프 사이클
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
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

        mainViewModel.clearData()
        mainViewModel.isEmployee.observe(viewLifecycleOwner) { isEmployee ->
            if(isEmployee == true || mainViewModel.userInfo.value!!.role == "OWNER") {
                orderBinding.fragmentOrderTvNoorder.visibility = View.GONE
                orderBinding.fragmentOrderRvOrder.visibility = View.VISIBLE
                orderBinding.fragmentOrderDateSpinner.visibility = View.VISIBLE

                // recipeList 준비 상태 확인
                mainViewModel.recipeList.observe(viewLifecycleOwner) { recipes ->
                    if (!recipes.isNullOrEmpty()) {
                        viewModel.getOrders()
                        //  초기화
                        resetData()
                        //어뎁터설정
                        initAdapter("")
                        recipeList = recipes
                        startAnimations()  // 데이터가 준비된 후 애니메이션 시작
                    } else {
                        // recipeList가 비어있으면 다시 요청
                        mainViewModel.getRecipeList()
                    }
                }
            } else {
                orderBinding.fragmentOrderTvNoorder.visibility = View.VISIBLE
                orderBinding.fragmentOrderRvOrder.visibility = View.GONE
                orderBinding.fragmentOrderDateSpinner.visibility = View.GONE
            }
        }
        mainViewModel.getIsEmployee(ApplicationClass.sharedPreferencesUtil.getUser().userId!!.toInt())

        startAnimations()
    }
    override fun onResume() {
        super.onResume()
        mainActivity.showBottomNavigation()
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // 화면 회전 잠금
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _orderBinding = null
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
            viewModel.orderList.observe(viewLifecycleOwner) { orderList ->
                orderAdapter.orderList.clear()
                orderAdapter.recipeNameList.clear()
                if(orderList.isEmpty()){
                    orderBinding.fragmentOrderTvNoorder.visibility = View.VISIBLE
                    orderBinding.fragmentOrderRvOrder.visibility = View.GONE
                    orderBinding.fragmentOrderDateSpinner.visibility = View.GONE
                }else {
                    orderBinding.fragmentOrderTvNoorder.visibility = View.GONE
                    orderBinding.fragmentOrderRvOrder.visibility = View.VISIBLE
                    orderBinding.fragmentOrderDateSpinner.visibility = View.VISIBLE
                    for (item in orderList) {
                        // ISO 8601 형식의 날짜를 파싱 (UTC 기준)
                        val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)
                        utcFormat.timeZone = TimeZone.getTimeZone("UTC") // UTC로 설정

                        val date = utcFormat.parse(item.orderDate)

                        // 변환된 날짜 출력
                        val formattedDate = utcFormat.format(date)
                        if (!orderDateList.contains(formattedDate.substring(0, 10))) {
                            orderDateList.add(formattedDate.substring(0, 10))
                            orderDateList.sortedDescending()
                            configureDateSpinner()
                        }
                        if (selectedDate.isNotBlank() && formattedDate.substring(0, 10) == selectedDate) {
                            if(!orderAdapter.orderList.contains(item)){
                                orderAdapter.orderList.add(item)
                                val details = item.orderDetails.sortedBy { it.recipeId }
                                orderAdapter.recipeNameList.add(recipeList.filter { details.first().recipeId == it.recipeId }.first().recipeName)
                            }
                        }
                    }
                }

                layoutAnimation = LayoutAnimationController(AnimationSet(true).apply {
                    val translate = TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, -1f,
                        Animation.RELATIVE_TO_SELF, 0f
                    ).apply {
                        duration = 500
                    }

                    val alpha = AlphaAnimation(0f, 1f).apply {
                        duration = 500
                    }

                    addAnimation(translate)
                    addAnimation(alpha)
                }).apply {
                    delay = 0.1f
                    order = LayoutAnimationController.ORDER_NORMAL
                }

                adapter = orderAdapter
            }
        }
    }

    private fun startAnimations() {
        // 1. 헤더 애니메이션 (이미지뷰와 텍스트)
        val headerViews = listOf(orderBinding.imageView)
        
        headerViews.forEach { view ->
            view.translationY = -50f
            view.animate()
                .translationY(0f)
                .setDuration(1000)
        }

        // 첫 번째 그룹 (상단 프로필 영역) - 투명도로 페이드인
        val firstGroup = listOf(orderBinding.tvJumunneyeok)

        firstGroup.forEach { view ->
            view.alpha = 0f
            view.animate()
                .alpha(1f)
                .setDuration(500)
                .setStartDelay(400)
                .withEndAction {
                    if (_orderBinding == null) return@withEndAction
                }
        }

        // 2. 스피너 애니메이션
        orderBinding.fragmentOrderDateSpinner.apply {
            translationX = -500f
            alpha = 0f
            animate()
                .translationX(0f)
                .alpha(1f)
                .setDuration(1000)
        }
    }
}
