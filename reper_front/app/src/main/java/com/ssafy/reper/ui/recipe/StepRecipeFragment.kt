package com.ssafy.reper.ui.recipe

import MainActivityViewModel
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ssafy.reper.R
import com.ssafy.reper.base.ApplicationClass
import com.ssafy.reper.data.dto.Order
import com.ssafy.reper.data.dto.OrderDetail
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.databinding.FragmentStepRecipeBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.util.ViewModelSingleton

private const val TAG = "StepRecipeFragment_정언"
class StepRecipeFragment : Fragment() {
    // 1. AllRecipe에서 넘어오면 세로만 가능하게 한다.
    // 2. OrderRecipe에서 넘어오면 가로 가능

    // 주문 리스트 recyclerView Adapter
    private lateinit var ingredientsAdapter: RecipeIngredientsAdapter

    var nowRecipeIdx = 0
    var nowStepIdx = 0
    var totalSteps = 0
    var totalRecipes =  0
    lateinit var order : Order
    lateinit var orderDetails: MutableList<OrderDetail>
    lateinit var selectedRecipeList : MutableList<Recipe>

    private val mainViewModel: MainActivityViewModel by lazy { ViewModelSingleton.mainActivityViewModel }
    private val viewModel: RecipeViewModel by viewModels()

    // Bundle 변수
    lateinit var recipeIdList:MutableList<Int>
    var whereAmICame = -1

    private lateinit var mainActivity: MainActivity

    private var _stepRecipeBinding : FragmentStepRecipeBinding? = null
    private val stepRecipeBinding get() =_stepRecipeBinding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED // 화면 회전 잠금 해제
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _stepRecipeBinding = FragmentStepRecipeBinding.inflate(inflater, container, false)
        return stepRecipeBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // OrderFragment에서 bundle로 던진 orderId를 받음
        whereAmICame = arguments?.getInt("whereAmICame") ?: -1 // 1 : AllRecipeFragment // 2 : OrderRecipeFragment
        recipeIdList = arguments?.getIntegerArrayList("idList")?.toMutableList() ?: mutableListOf()
        mainViewModel.setSelectedRecipes(recipeIdList)

        // 전역변수 관리
        mainViewModel.selectedRecipeList.observe(viewLifecycleOwner){
            Log.d(TAG, "selectedRecipeList: $it")
            selectedRecipeList = it
            totalRecipes = it.count()

            // 공통 이벤트 처리
            initEvent()

            val orientation = resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) { // 가로모드 처리
                eventLand()
            } else { // 세로 모드 처리
                eventPortrait()
            }
        }
        mainViewModel.nowISeeRecipe.observe(viewLifecycleOwner){
            Log.d(TAG, "nowISeeRecipe: ")
            nowRecipeIdx = it
        }
        mainViewModel.nowISeeStep.observe(viewLifecycleOwner){
            Log.d(TAG, "nowISeeStep: ")
            nowStepIdx = it
        }
        mainViewModel.recipeSteps.observe(viewLifecycleOwner){
            Log.d(TAG, "recipeSteps: ")
            totalSteps = it.count()
        }

        if(whereAmICame == 1){ // ALlRecipeFragment에서 옴
            mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // 화면 회전 잠금
        }
        else if(whereAmICame == 2) { // OrderRecipeFragment에서 옴
            mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED // 화면 회전 잠금 해제
            order = mainViewModel.order.value!!
            orderDetails = order.orderDetails
        }


    }
    //캡쳐방지 코드입니다! 메시지 내용은 수정불가능,, 핸드폰내에 저장된 메시지가 뜨는 거라고 하네요
    override fun onResume() {
        super.onResume()
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        mainActivity.hideBottomNavigation()
    }
    override fun onPause() {
        super.onPause()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
    ////////////////////////////////////////////////////////////////////////////////////////
    override fun onDestroyView() {
        super.onDestroyView()
        _stepRecipeBinding = null
    }

    fun initEvent(){
        // 돌아가기 버튼
        stepRecipeBinding.steprecipeFmBtnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 다음버튼이 눌릴 때.
        stepRecipeBinding.steprecipeFmBtnRight.setOnClickListener {
            nextEvent()
        }

        // 이전 버튼이 눌릴 때.
        stepRecipeBinding.steprecipeFmBtnLeft.setOnClickListener {
            prevEvent()
        }

        // 이전, 다음 버튼 Visible 처리 (화면이 회전되어 재구성될 가능성..)
        if(nowRecipeIdx == 0 && nowStepIdx == -1){
            stepRecipeBinding.steprecipeFmBtnLeft.visibility = View.GONE
            stepRecipeBinding.steprecipeFmBtnRight.visibility = View.VISIBLE
        }
        else if(nowRecipeIdx >= totalRecipes - 1 && nowStepIdx >= totalSteps - 1){
            stepRecipeBinding.steprecipeFmBtnLeft.visibility = View.VISIBLE
            stepRecipeBinding.steprecipeFmBtnRight.visibility = View.GONE
        }
        else{
            stepRecipeBinding.steprecipeFmBtnLeft.visibility = View.VISIBLE
            stepRecipeBinding.steprecipeFmBtnRight.visibility = View.VISIBLE
        }

        // 추가사항 처리
        if(whereAmICame == 1){
            stepRecipeBinding.constraintLayout2.visibility = View.GONE // 추가사항 안보이게
        }else{
            stepRecipeBinding.constraintLayout2.visibility = View.VISIBLE // 추가사항 보이게
            stepRecipeBinding.steprecipeFmTvCustom.setText(orderDetails[nowRecipeIdx].customerRequest)
        }
    }
    fun eventPortrait(){
        stepRecipeBinding.steprecipeFmTvUser?.setText("이용자 : ${ApplicationClass.sharedPreferencesUtil.getUser().userId.toString()}")
        stepRecipeBinding.steprecipeFmTvMenuName?.text = "${selectedRecipeList.get(nowRecipeIdx).recipeName} ${selectedRecipeList.get(nowRecipeIdx).type}"

        if(nowStepIdx == -1){ // 재료를 보여줘야해!
            showIngredient(nowRecipeIdx)
        }
        else{ // 레시피를 보여줘야해!
            showOneStepRecipe(nowStepIdx)
        }
    }
    fun eventLand(){
        stepRecipeBinding.steprecipeFmLandTvUser?.setText("이용자 : ${ApplicationClass.sharedPreferencesUtil.getUser().userId.toString()}")

        if(whereAmICame == 1){
            stepRecipeBinding.constraintLayout4?.visibility = View.GONE // 인덱스 안보이게
        }
        else if(whereAmICame == 2){
            stepRecipeBinding.constraintLayout4?.visibility = View.VISIBLE // 인덱스 보이게
            stepRecipeBinding.steprecipeFmTvIndex.let{ it->
                var quantities = 0
                for(item in orderDetails){
                    quantities += item.quantity
                }
                if(order.takeout){
                    it?.setText("포장 ${quantities}개")
                    stepRecipeBinding.imageView?.setImageResource(R.drawable.steprecipe_land_red_index)
                }
                else{
                    it?.setText("매장 ${quantities}개")
                    stepRecipeBinding.imageView?.setImageResource(R.drawable.steprecipe_land_green_index)
                }
            }
        }

        if(nowStepIdx == -1){ // 재료를 보여줘야해!
            showIngredient(nowRecipeIdx)
        }
        else{ // 레시피를 보여줘야해!
            showOneStepRecipe(nowStepIdx)
        }
    }
    // 1. 내가 지금 재료라서 스탭을 보여줘야 할 때
    // 2. 내가 지금 스텝인데, 다음 스텝이 있을 때
    // 3. 내가 지금 스텝인데, 마지막 스텝이라 다음 레시피의 재료를 보여줘야할 때
    // 4. 내가 지금 스텝인데, 마지막 레시피의 마지막 스탭일때
    fun nextEvent() {
        stepRecipeBinding.steprecipeFmBtnLeft.visibility = View.VISIBLE

        mainViewModel.setNowISeeStep(nowStepIdx + 1)
        Log.d(TAG, "다음을 눌렀습니다. ${nowRecipeIdx}/${totalRecipes}, ${nowStepIdx}/${totalSteps}")
        when {
            // 다음 스텝이 존재하는 경우 → 다음 스텝으로 이동
            nowStepIdx < totalSteps - 1 -> {
                showOneStepRecipe(nowStepIdx)
            }

            nowStepIdx >= totalSteps - 1 && nowRecipeIdx < totalRecipes - 1-> {
                Log.d(TAG, "다음 레시피 보여주기! ${nowRecipeIdx}/${totalRecipes}, ${nowStepIdx}/${totalSteps}")
                mainViewModel.setNowISeeRecipe(nowRecipeIdx + 1)
                mainViewModel.setRecipeSteps(nowRecipeIdx)
                mainViewModel.setNowISeeStep(-1)
                showIngredient(nowRecipeIdx)
            }

            // 마지막 레시피의 마지막 스텝인 경우 → 버튼 비활성화
            else -> {
                Log.d(TAG, "마지막 레시피의 마지막 스텝 도달 ${nowRecipeIdx}/${totalRecipes}, ${nowStepIdx}/${totalSteps}")
                showOneStepRecipe(nowStepIdx)
                stepRecipeBinding.steprecipeFmBtnRight.visibility = View.GONE
            }
        }
    }

    // 1. 내가 지금 재료라서 이전 레시피의 마지막 스탭을 보여줘야 할 때
    // 2. 내가 지금 스텝인데, 이전 스텝이 있을 때
    // 3. 내가 지금 스텝인데, 첫번쨰 스탭이라 현재 레시피의 재료를 보여줘야할 때
    // 4. 내가 재료인데, 첫번쨰 레시피의 재료일때
    fun prevEvent(){
        stepRecipeBinding.steprecipeFmBtnRight.visibility = View.VISIBLE

        mainViewModel.setNowISeeStep(nowStepIdx - 1)
//        Log.d(TAG,"이전을 눌렀습니다. ${mainViewModel.nowISeeRecipe.value} / ${mainViewModel.nowISeeStep.value}")
        when {
            // 이전 스텝이 존재하는 경우 → 이전 스텝으로 이동
            nowStepIdx >= 0-> {
                showOneStepRecipe(nowStepIdx)
            }

            nowStepIdx < 0 && nowRecipeIdx > 0-> {
                showIngredient(nowRecipeIdx)
                mainViewModel.setNowISeeRecipe(nowRecipeIdx - 1)
                mainViewModel.setRecipeSteps(nowRecipeIdx)
                mainViewModel.setNowISeeStep(totalSteps - 1)
            }

            nowStepIdx < 0  && nowRecipeIdx == 0->{
                mainViewModel.setNowISeeStep(-1)
                stepRecipeBinding.steprecipeFmBtnLeft.visibility = View.GONE
                showIngredient(nowRecipeIdx)
            }

            else ->{
                Log.d(TAG, "prevEvent: 엥 이게 무슨 조건이지? $nowStepIdx / $nowRecipeIdx")
            }
        }
    }
    // 재료 보이게
    fun showIngredient(recipeIdx:Int){
        stepRecipeBinding.lottieAnimationView.visibility = View.GONE
        stepRecipeBinding.steprecipeFmTvStep?.visibility = View.GONE
        stepRecipeBinding.steprecipeFmTvMenuName?.setText("${selectedRecipeList.get(recipeIdx).recipeName} ${selectedRecipeList.get(recipeIdx).type}")
        stepRecipeBinding.steprecipeFmLandTvRecipe?.setText("${selectedRecipeList.get(recipeIdx).recipeName} ${selectedRecipeList.get(recipeIdx).type}")

        if(whereAmICame == 2){
            stepRecipeBinding.steprecipeFmTvCustom.setText("${orderDetails.get(recipeIdx).customerRequest}")
        }

        stepRecipeBinding.steprecipeFmRvIngredients.visibility = View.VISIBLE
        initIngredientsAdapter(recipeIdx)
    }
    // 레시피 보이게
    fun showOneStepRecipe(stepIdx:Int){
        val recipeSteps = mainViewModel.recipeSteps.value!!

        stepRecipeBinding.steprecipeFmRvIngredients.visibility = View.GONE
        if(whereAmICame == 2){
            stepRecipeBinding.steprecipeFmTvCustom.setText("${orderDetails.get(nowRecipeIdx).customerRequest}")
        }

        // 로티
        stepRecipeBinding.lottieAnimationView.visibility = View.VISIBLE
        stepRecipeBinding.lottieAnimationView.setAnimationFromUrl(recipeSteps.get(stepIdx)?.animationUrl)
        // 레시피
        stepRecipeBinding.steprecipeFmTvStep?.visibility = View.VISIBLE
        stepRecipeBinding.steprecipeFmTvStep?.setText(recipeSteps?.get(stepIdx)?.instruction)
        stepRecipeBinding.steprecipeFmLandTvRecipe?.setText(recipeSteps?.get(stepIdx)?.instruction)
    }

    // 재료 어뎁터 설정
    fun initIngredientsAdapter(recipeIdx:Int) {
        val ingredients = mainViewModel.selectedRecipeList.value?.get(recipeIdx)?.ingredients!!

        ingredientsAdapter = RecipeIngredientsAdapter(ingredients)
        stepRecipeBinding.steprecipeFmRvIngredients.adapter = ingredientsAdapter
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val bundle = arguments
        findNavController().popBackStack(R.id.stepRecipeFragment, true)

        initEvent()
        // 가로모드 UI 업데이트
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            findNavController().navigate(R.id.stepRecipeFragment, bundle)
            eventLand()
        } else { // 세로모드 UI 업데이트
            eventPortrait()
            findNavController().navigate(R.id.stepRecipeFragment, bundle)
        }
    }

}