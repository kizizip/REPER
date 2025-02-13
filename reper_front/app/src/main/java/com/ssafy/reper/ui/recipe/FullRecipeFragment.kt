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
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.ssafy.reper.R
import com.ssafy.reper.base.ApplicationClass
import com.ssafy.reper.data.dto.Order
import com.ssafy.reper.data.dto.OrderDetail
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.data.dto.RecipeStep
import com.ssafy.reper.databinding.FragmentFullRecipeBinding
import com.ssafy.reper.databinding.FragmentFullRecipeItemBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.util.ViewModelSingleton

private const val TAG = "FullRecipeFragment_정언"
class FullRecipeFragment : Fragment() {

    var nowRecipeIdx = 0
    var totalRecipes =  0
    lateinit var recipeSteps:MutableList<String>
    lateinit var order : Order
    lateinit var orderDetails: MutableList<OrderDetail>
    lateinit var selectedRecipeList : MutableList<Recipe>

    // Bundle 변수
    var whereAmICame = -1

    private val mainViewModel: MainActivityViewModel by lazy { ViewModelSingleton.mainActivityViewModel }
    private val viewModel: RecipeViewModel by viewModels()

    private lateinit var slidingUpPanelLayout: SlidingUpPanelLayout
    private lateinit var scrollView: LockableNestedScrollView

    // 레시피 리스트 recyclerView Adapter
    private lateinit var fullRecipeListAdapter: FullRecipeListAdapter

    private lateinit var mainActivity: MainActivity

    private var _fullRecipeItemBinding : FragmentFullRecipeItemBinding? = null
    private val fullRecipeItemBinding get() =_fullRecipeItemBinding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fullRecipeItemBinding = FragmentFullRecipeItemBinding.inflate(inflater, container, false)
        return fullRecipeItemBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 내가 어느 Fragment에서 왔는 지 Flag 처리
        whereAmICame = arguments?.getInt("whereAmICame") ?: -1 // 1 : AllRecipeFragment // 2 : OrderRecipeFragment

        if(whereAmICame == 2) { // OrderRecipeFragment에서 옴
            order = mainViewModel.order.value!!
            orderDetails = order.orderDetails
        }

        // 전역변수 관리
        mainViewModel.nowISeeRecipe.observe(viewLifecycleOwner){
            if (it != null) {
                nowRecipeIdx = it
            }
            Log.d(TAG, "onViewCreated: nowISeeRecipe: $it")
        }
        mainViewModel.recipeSteps.observe(viewLifecycleOwner){
            if (it != null) {
                recipeSteps.clear()
                for(recipeStep in it){
                    recipeSteps.add(recipeStep.instruction)
                }
            }
            Log.d(TAG, "onViewCreated: recipeSteps: $it")
        }
        mainViewModel.isDataReady.observe(viewLifecycleOwner){
            if(it){
                selectedRecipeList = mainViewModel.selectedRecipeList.value!!
                totalRecipes = selectedRecipeList.distinctBy { it.recipeName }.count()

                // 공통 이벤트 처리
                initEvent()
                // RecyclerView adapter 처리
                initAdapter()
            }
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
        _fullRecipeItemBinding = null
    }

    fun initEvent(){
        // 즐겨찾기 버튼이 눌리면
        fullRecipeItemBinding.fullrecipeFmBtnHeart.setOnClickListener {
            if(fullRecipeItemBinding.fullrecipeFmIvLineheart.visibility == View.VISIBLE){
                fullRecipeItemBinding.fullrecipeFmIvLineheart.visibility = View.GONE
                fullRecipeItemBinding.fullrecipeFmIvFullheart.visibility = View.VISIBLE
                viewModel.likeRecipe(ApplicationClass.sharedPreferencesUtil.getUser().userId!!, selectedRecipeList.get(nowRecipeIdx).recipeId)
            }
            else{
                fullRecipeItemBinding.fullrecipeFmIvLineheart.visibility = View.VISIBLE
                fullRecipeItemBinding.fullrecipeFmIvFullheart.visibility = View.GONE
                viewModel.unLikeRecipe(ApplicationClass.sharedPreferencesUtil.getUser().userId!!, selectedRecipeList.get(nowRecipeIdx).recipeId)
            }
        }

        // Ice Hot 버튼 클릭
        fullRecipeItemBinding.fullrecipeFmBtnIce.setOnClickListener {
            fullRecipeItemBinding.fullrecipeFmBtngroup.check(fullRecipeItemBinding.fullrecipeFmBtnIce.id)
            btnHotIceColorChange()
        }
        fullRecipeItemBinding.fullrecipeFmBtnHot.setOnClickListener {
            fullRecipeItemBinding.fullrecipeFmBtngroup.check(fullRecipeItemBinding.fullrecipeFmBtnHot .id)
            btnHotIceColorChange()
        }

        // slidepannel이 다 펴질 때만 scroll 가능하게!
        slidingUpPanelLayout = fullRecipeItemBinding.fullrecipeFmSlideuppanel // XML의 SlidingUpPanelLayout id
        scrollView = fullRecipeItemBinding.scrollView // XML의 NestedScrollView id
        scrollView.isScrollable = false
        slidingUpPanelLayout.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View, slideOffset: Float) {
                scrollView.isScrollable = slideOffset == 1.0f
            }
            override fun onPanelStateChanged(panel: View, previousState: SlidingUpPanelLayout.PanelState, newState: SlidingUpPanelLayout.PanelState) {
                // 상태 변화에 따른 추가 처리가 필요하면 여기에 작성
            }
        })
    }
    fun initAdapter() {
        // allrecipe item 클릭 이벤트 리스너
        fullRecipeListAdapter = FullRecipeListAdapter(mutableListOf()) { position ->
            // StepRecipeFragment로 이동하는 거 필요해!!!!!!!!!!!!!!!!!!!!!!!
            findNavController().navigate(R.id.stepRecipeFragment)
        }

        fullRecipeItemBinding.fullrecipeFmRvRecipe.apply {
            layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            fullRecipeListAdapter.recipeStepList = recipeSteps
            adapter = fullRecipeListAdapter
        }
    }
    fun btnHotIceColorChange(){
        if(fullRecipeItemBinding.fullrecipeFmBtngroup.checkedButtonId == fullRecipeItemBinding.fullrecipeFmBtnIce.id){
            fullRecipeItemBinding.fullrecipeFmBtnIce.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.green))
            fullRecipeItemBinding.fullrecipeFmBtnIce.setTextColor(ContextCompat.getColor(mainActivity, R.color.white))
            fullRecipeItemBinding.fullrecipeFmBtnHot.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.white))
            fullRecipeItemBinding.fullrecipeFmBtnHot.setTextColor(ContextCompat.getColor(mainActivity, R.color.green))
        }

        if(fullRecipeItemBinding.fullrecipeFmBtngroup.checkedButtonId == fullRecipeItemBinding.fullrecipeFmBtnHot.id){
            fullRecipeItemBinding.fullrecipeFmBtnIce.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.white))
            fullRecipeItemBinding.fullrecipeFmBtnIce.setTextColor(ContextCompat.getColor(mainActivity, R.color.green))
            fullRecipeItemBinding.fullrecipeFmBtnHot.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.green))
            fullRecipeItemBinding.fullrecipeFmBtnHot.setTextColor(ContextCompat.getColor(mainActivity, R.color.white))
        }
    }
}