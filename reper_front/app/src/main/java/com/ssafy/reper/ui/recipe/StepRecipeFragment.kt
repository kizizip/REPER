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

private const val TAG = "StepRecipeFragment_정언"
class StepRecipeFragment : Fragment() {
    // 1. AllRecipe에서 넘어오면 세로만 가능하게 한다.
    // 2. OrderRecipe에서 넘어모녀

    private val mainViewModel: MainActivityViewModel by lazy { ViewModelSingleton.mainActivityViewModel }
    private val viewModel: RecipeViewModel by viewModels()

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
            Log.d(TAG, "다음을 눌렀습니다.")
            stepRecipeBinding.steprecipeFmBtnLeft.visibility = View.VISIBLE
            // 재료를 보여주고 있다면?
            if(stepRecipeBinding.lottieAnimationView.visibility == View.GONE){
                showOneStepRecipePortrait(mainViewModel.nowISeeRecipe.value!!, mainViewModel.nowISeeStep.value!!)
            }
            else{
                // 현재 페이지가 마지막 레시피의 마지막 스탭일 때.
                if(mainViewModel.selectedRecipeList.value?.get(mainViewModel.nowISeeRecipe.value!!)?.recipeSteps?.size!! > mainViewModel.nowISeeStep.value!!
                    &&  mainViewModel.selectedRecipeList.value!!.size > mainViewModel.nowISeeRecipe.value!!){
                    // 다음버튼 안 보이게
                    stepRecipeBinding.steprecipeFmBtnRight.visibility = View.GONE
                    showOneStepRecipePortrait(mainViewModel.nowISeeRecipe.value!!, mainViewModel.nowISeeStep.value!!.plus(1))
                }
                // 아직 다음 레시피가 있을 때.
                else{
                    Log.d(TAG, "아직 다음 레시피가 있습니다.")
                    // 현재 레시피의 마지막 스탭일 떄.
                    if(mainViewModel.selectedRecipeList.value?.get(mainViewModel.nowISeeRecipe.value!!)?.recipeSteps?.size == mainViewModel.nowISeeStep.value){
                        Log.d(TAG, "현재 레시피의 마지막 스탭입니다.")
                        // 다음 레시피를 본다.
                        mainViewModel.setNowISeeRecipe(mainViewModel.nowISeeRecipe.value?.plus(1)!!)
                        mainViewModel.setNowISeeStep(0)

                        // 메뉴명 변경
                        stepRecipeBinding.steprecipeFmTvMenuName?.setText(mainViewModel.selectedRecipeList.value?.get(mainViewModel.nowISeeRecipe.value!!)!!.recipeName)
                        // 재료 출력
                        showIngredientPortrait(mainViewModel.nowISeeRecipe.value!!)
                    }
                    // 아직 다음 스탭이 남아있을 때.
                    else{
                        Log.d(TAG, "아직 스탭이 남아있습니다.")
                        // 현재 레시피의 다음 스탭을 본다.
                        mainViewModel.setNowISeeStep(mainViewModel.nowISeeStep.value?.plus(1)!!)
                        showOneStepRecipePortrait(mainViewModel.nowISeeRecipe.value!!, mainViewModel.nowISeeStep.value!!)
                    }
                }
            }

        }


        // 1. 이전 스텝이 남앗을 때 -> 이전 스텝을 보여준다.
        // 2. 내가 지금 첫번째 스텝이라 이전을 누르면 재료를 보여줘야할 때
        // 3. 내가 지금 첫번째 레시피의 첫번째 스탭일 때 -> 이전버튼을 안보이게, 재료를 보여줌
        // 4. 내가 지금 재료라 이전 레시피의 마지막 스텝을 보여줘야할때.
        // 이전 버튼이 눌릴 때.
        stepRecipeBinding.steprecipeFmBtnLeft.setOnClickListener {
            Log.d(TAG, "이전을 눌렀습니다.")
            stepRecipeBinding.steprecipeFmBtnRight.visibility = View.VISIBLE
        }
    }
    fun initEventLand(){

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
    }
    // 레시피 보이게
    fun showOneStepRecipePortrait(recipeIdx:Int, stepIdx:Int){
        val recipeSteps = mainViewModel.selectedRecipeList.value?.get(recipeIdx)?.recipeSteps

        //로티
        stepRecipeBinding.lottieAnimationView.visibility = View.VISIBLE
        stepRecipeBinding.lottieAnimationView.setAnimationFromUrl(recipeSteps?.get(stepIdx)?.animationUrl)
        stepRecipeBinding.steprecipeFmTvIngredient?.visibility = View.VISIBLE
        stepRecipeBinding.steprecipeFmTvIngredient?.text = recipeSteps?.get(stepIdx)?.instruction
    }
}