package com.ssafy.reper.ui.recipe

import MainActivityViewModel
import android.content.Context
import android.content.pm.ActivityInfo
import and roid.content.res.Configuration
import android.os.Bundle
import android.system.Os
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
        mainViewModel.getSelectedRecipes(ApplicationClass.sharedPreferencesUtil.getStoreId(), recipeIdList)
        if(whereAmICame == 1){
            mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // 화면 회전 잠금
        }
        else if(whereAmICame == 2){
            mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED // 화면 회전 잠금 해제
        }

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 가로 모드일 때의 추가 UI 처리
            initEventLand()
        } else {
            // 세로 모드일 때의 추가 UI 처리
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

        stepRecipeBinding.steprecipeFmBtnRight.setOnClickListener {
            mainViewModel.setNowISeeStep(mainViewModel.nowISeeStep.value?.plus(1) ?: 0)
            // 마지막 레시피의 마지막 스탭일 때.
            if(mainViewModel.selectedRecipeList.value?.get(mainViewModel.nowISeeRecipe.value!!)?.recipeSteps?.size == mainViewModel.nowISeeStep.value
                &&  mainViewModel.selectedRecipeList.value!!.size == mainViewModel.nowISeeRecipe.value){
                stepRecipeBinding.steprecipeFmBtnRight.visibility = View.GONE
            }

            if(mainViewModel.selectedRecipeList.value?.get(mainViewModel.nowISeeRecipe.value!!)?.recipeSteps?.size == mainViewModel.nowISeeStep.value){
                
            }
        }
    }
    fun initEventLand(){

    }
    fun showIngredientPortrait(recipeIdx:Int){
        val ingredients = mainViewModel.selectedRecipeList.value?.get(recipeIdx)?.ingredients
        var ingredientsText = "${ingredients?.first()?.ingredientName}"
        if (ingredients != null) {
            for(item in ingredients){
                ingredientsText += ", ${item.ingredientName}"
            }
        }
    }
    fun showOneStepRecipePortrait(recipeIdx:Int, stepIdx:Int){
        val recipeSteps = mainViewModel.selectedRecipeList.value?.get(recipeIdx)?.recipeSteps

        stepRecipeBinding.lottieAnimationView.visibility = View.VISIBLE
        stepRecipeBinding.lottieAnimationView.setAnimationFromUrl(recipeSteps?.get(stepIdx)?.animationUrl)
        stepRecipeBinding.steprecipeFmTvIngredient?.visibility = View.VISIBLE
        stepRecipeBinding.steprecipeFmTvIngredient?.text = recipeSteps?.get(stepIdx)?.instruction
    }
}