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
import com.ssafy.reper.base.ApplicationClass
import com.ssafy.reper.databinding.FragmentStepRecipeBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.util.ViewModelSingleton
import kotlin.math.min

private const val TAG = "StepRecipeFragment_정언"
class StepRecipeFragment : Fragment() {
    // 1. AllRecipe에서 넘어오면 세로만 가능하게 한다.
    // 2. OrderRecipe에서 넘어모녀

    private val mainViewModel: MainActivityViewModel by lazy { ViewModelSingleton.mainActivityViewModel }
    private val viewModel: RecipeViewModel by viewModels()

    // Bundle 변수
    lateinit var recipeIdList:ArrayList<Int>
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
        recipeIdList = arguments?.getIntegerArrayList("recipeIdList") ?: arrayListOf(-1)

        // 선택되어 들어온 Recipe를 받아옴
        mainViewModel.getSelectedRecipes(ApplicationClass.sharedPreferencesUtil.getStoreId(), recipeIdList)

        if(whereAmICame == 1){ // ALlRecipeFragment에서 옴
            mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // 화면 회전 잠금
        }
        else if(whereAmICame == 2){ // OrderRecipeFragment에서 옴
            mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED // 화면 회전 잠금 해제
        }

        // 가로 모드
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            initEventLand()
        }
        // 세로 모드
        else {
            initEventPortrait()
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

    fun initEventPortrait(){
        stepRecipeBinding.steprecipeFmBtnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        stepRecipeBinding.steprecipeFmBtnLeft.visibility = View.GONE

        mainViewModel.selectedRecipeList.observe(viewLifecycleOwner){
            var ingredients = "${it.first().ingredients.first().ingredientName}"
            for(item in it.first().ingredients){
                ingredients += ", ${item.ingredientName}"
            }

            stepRecipeBinding.steprecipeFmTvIndex.visibility = View.GONE
            stepRecipeBinding.lottieAnimationView.visibility = View.GONE
            stepRecipeBinding.constraintLayout2.visibility = View.GONE // 추가사항 안보이게
            stepRecipeBinding.steprecipeFmTvMenuName?.text = it.first().recipeName
            stepRecipeBinding.steprecipeFmTvIngredient?.text = ingredients
        }

        // 다음버튼이 눌릴 때.
        stepRecipeBinding.steprecipeFmBtnRight.setOnClickListener {
            nextEvent()
        }

        // 이전 버튼이 눌릴 때.
        stepRecipeBinding.steprecipeFmBtnLeft.setOnClickListener {
            prevEvent()
        }
    }
    fun initEventLand(){

    }
    // 1. 내가 지금 재료라서 스탭을 보여줘야 할 때
    // 2. 내가 지금 스텝인데, 다음 스텝이 있을 때
    // 3. 내가 지금 스텝인데, 마지막 스텝이라 다음 레시피의 재료를 보여줘야할 때
    // 4. 내가 지금 스텝인데, 마지막 레시피의 마지막 스탭일때
    fun nextEvent(){
//        Log.d(TAG, "다음을 눌렀습니다. ${mainViewModel.nowISeeRecipe.value} / ${mainViewModel.nowISeeStep.value}")
        stepRecipeBinding.steprecipeFmBtnLeft.visibility = View.VISIBLE

        // 내가 지금 재료야! -> 다음 스탭 보여줘야함
        if(stepRecipeBinding.lottieAnimationView.visibility == View.GONE){
            mainViewModel.setNowISeeStep(mainViewModel.nowISeeStep.value!! + 1)
//            Log.d(TAG, "나 재료야! ${mainViewModel.nowISeeRecipe.value} / ${mainViewModel.nowISeeStep.value}")
            showOneStepRecipePortrait(mainViewModel.nowISeeRecipe.value!!)
        }
        // 내가 지금 스탭이야
        else{
            // 다음 스탭이 있어!
            if(mainViewModel.nowISeeStep.value!! < mainViewModel.recipeSteps.value!!.count() - 2){
                mainViewModel.setNowISeeStep(mainViewModel.nowISeeStep.value!! + 1)
//                Log.d(TAG, "나 다음 스탭 있어! ${mainViewModel.nowISeeRecipe.value} / ${mainViewModel.nowISeeStep.value}")
                showOneStepRecipePortrait(mainViewModel.nowISeeStep.value!!)
            }
            // 마지막 스탭인데, 다음 레시피가 있어!
            else if(mainViewModel.nowISeeRecipe.value!! < mainViewModel.selectedRecipeList.value!!.count()  - 1){
                mainViewModel.setNowISeeRecipe(mainViewModel.nowISeeRecipe.value!! + 1)
                mainViewModel.setRecipeSteps(mainViewModel.nowISeeRecipe.value!!)
                mainViewModel.setNowISeeStep(-1)
//                Log.d(TAG, "나 다음 레시피 있어! ${mainViewModel.nowISeeRecipe.value} / ${mainViewModel.nowISeeStep.value}")
                showIngredientPortrait(mainViewModel.nowISeeRecipe.value!!)
            }
            // 내가 마지막 레시피의 마지막 스탭이야!
            else{
//                Log.d(TAG, "나 마지막 레시피의 마지막 스탭이야! ${mainViewModel.nowISeeRecipe.value} / ${mainViewModel.nowISeeStep.value}")
                mainViewModel.setNowISeeStep(mainViewModel.nowISeeStep.value!! + 1)
                showOneStepRecipePortrait(mainViewModel.nowISeeStep.value!!)
                stepRecipeBinding.steprecipeFmBtnRight.visibility = View.GONE
            }
        }
    }
    // 1. 내가 지금 재료라서 이전 레시피의 마지막 스탭을 보여줘야 할 때
    // 2. 내가 지금 스텝인데, 이전 스텝이 있을 때
    // 3. 내가 지금 스텝인데, 첫번쨰 스탭이라 현재 레시피의 재료를 보여줘야할 때
    // 4. 내가 재료인데, 첫번쨰 레시피의 재료일때
    fun prevEvent(){
//        Log.d(TAG,"이전을 눌렀습니다. ${mainViewModel.nowISeeRecipe.value} / ${mainViewModel.nowISeeStep.value}")
        stepRecipeBinding.steprecipeFmBtnRight.visibility = View.VISIBLE

        // 난 스탭인데!
        if(stepRecipeBinding.lottieAnimationView.visibility == View.VISIBLE){
            // 이전 스탭이 있어!
            if(mainViewModel.nowISeeStep.value!! > 0){
                mainViewModel.setNowISeeStep(mainViewModel.nowISeeStep.value!! - 1)
//                Log.d(TAG, "나 이전에 스탭이 있어! ${mainViewModel.nowISeeRecipe.value} / ${mainViewModel.nowISeeStep.value}")
                showOneStepRecipePortrait(mainViewModel.nowISeeStep.value!!)
            }
            // 나 첫번째 스탭이라 재료 보여줘야해!
            else{
                mainViewModel.setNowISeeStep(-1)
//                Log.d(TAG, "나 첫번째 스탭이라 이제 재료 보여야 해! ${mainViewModel.nowISeeRecipe.value} / ${mainViewModel.nowISeeStep.value}")
                showIngredientPortrait(mainViewModel.nowISeeRecipe.value!!)
                // 나 이전 레시피가 있을 때!
                if(mainViewModel.nowISeeRecipe.value!! <=  0){
//                    Log.d(TAG, "나 첫번째 레시피의 재료야! ${mainViewModel.nowISeeRecipe.value} / ${mainViewModel.nowISeeStep.value}")
                    stepRecipeBinding.steprecipeFmBtnLeft.visibility = View.GONE
                }
            }
        }
        //내가 재료인데!
        else{
            // 나 이전 레시피가 있을 때!
            if(mainViewModel.nowISeeRecipe.value!! > 0){
                mainViewModel.setNowISeeRecipe(mainViewModel.nowISeeRecipe.value!! - 1)
                mainViewModel.setRecipeSteps(mainViewModel.nowISeeRecipe.value!!)
                mainViewModel.setNowISeeStep(mainViewModel.recipeSteps.value!!.size - 1)
//                Log.d(TAG, "나 재료인데, 이전 레시피가 있어! ${mainViewModel.nowISeeRecipe.value} / ${mainViewModel.nowISeeStep.value}")
                showOneStepRecipePortrait(mainViewModel.nowISeeStep.value!!)
            }
            // 첫번째 레시피의 재료일 때
            else{
//                Log.d(TAG, "나 첫번째 레시피의 재료야! ${mainViewModel.nowISeeRecipe.value} / ${mainViewModel.nowISeeStep.value}")
                stepRecipeBinding.steprecipeFmBtnLeft.visibility = View.GONE
            }
        }
    }
    // 재료 보이게
    fun showIngredientPortrait(recipeIdx:Int){
        stepRecipeBinding.lottieAnimationView.visibility = View.GONE
        val ingredients = mainViewModel.selectedRecipeList.value?.get(recipeIdx)?.ingredients
        var ingredientsText = "${ingredients?.first()?.ingredientName}"
        if (ingredients != null) {
            for(item in ingredients){
                ingredientsText += ", ${item.ingredientName}"
            }
        }
        stepRecipeBinding.steprecipeFmTvIngredient?.setText(ingredientsText)
    }
    // 레시피 보이게
    fun showOneStepRecipePortrait(stepIdx:Int){
        val recipeSteps = mainViewModel.recipeSteps.value!!

        // 로티
        stepRecipeBinding.lottieAnimationView.visibility = View.VISIBLE
        stepRecipeBinding.lottieAnimationView.setAnimationFromUrl(recipeSteps.get(stepIdx)?.animationUrl)
        // 레시피
        stepRecipeBinding.steprecipeFmTvIngredient?.visibility = View.VISIBLE
        stepRecipeBinding.steprecipeFmTvIngredient?.text = recipeSteps?.get(stepIdx)?.instruction
    }
}