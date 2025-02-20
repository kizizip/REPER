package com.ssafy.reper.ui.recipe

import MainActivityViewModel
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.ssafy.reper.R
import com.ssafy.reper.base.ApplicationClass
import com.ssafy.reper.data.dto.FavoriteRecipe
import com.ssafy.reper.data.dto.Order
import com.ssafy.reper.data.dto.OrderDetail
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.data.dto.RecipeStep
import com.ssafy.reper.databinding.FragmentFullRecipeBinding
import com.ssafy.reper.databinding.FragmentFullRecipeItemBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.recipe.adapter.FullRecipeListAdapter
import com.ssafy.reper.ui.recipe.adapter.FullRecipeViewPagerAdapter
import com.ssafy.reper.util.ViewModelSingleton
import androidx.viewpager2.widget.ViewPager2

private const val TAG = "FullRecipeFragment_정언"
class FullRecipeFragment : Fragment() {

    var nowRecipeIdx = 0
    var totalRecipes =  0
    var recipeSteps:MutableList<String> = mutableListOf()
    lateinit var order : Order
    var orderDetails: MutableList<OrderDetail> = mutableListOf()
    var selectedRecipeList : MutableList<Recipe> = mutableListOf()
    var favoriteReicpeList : MutableList<FavoriteRecipe> = mutableListOf()

    // Bundle 변수
    var whereAmICame = -1

    private val mainViewModel: MainActivityViewModel by lazy { ViewModelSingleton.mainActivityViewModel }
    private val viewModel: RecipeViewModel by viewModels()

    private lateinit var slidingUpPanelLayout: SlidingUpPanelLayout
    private lateinit var scrollView: LockableNestedScrollView

    // 레시피 리스트 recyclerView Adapter
    private lateinit var viewPagerAdapter: FullRecipeViewPagerAdapter

    private lateinit var mainActivity: MainActivity

    private var _fullRecipeBinding : FragmentFullRecipeBinding? = null
    private val fullRecipeBinding get() =_fullRecipeBinding!!
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
        _fullRecipeBinding = FragmentFullRecipeBinding.inflate(inflater, container, false)
        _fullRecipeItemBinding = FragmentFullRecipeItemBinding.inflate(inflater, container, false)
        return fullRecipeBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 내가 어느 Fragment에서 왔는 지 Flag 처리
        whereAmICame = arguments?.getInt("whereAmICame") ?: -1 // 1 : AllRecipeFragment // 2 : OrderRecipeFragment
        Log.d(TAG, "whereAmICame: ${whereAmICame}")

        if(whereAmICame == 2) { // OrderRecipeFragment에서 옴
            order = mainViewModel.order.value!!
            orderDetails = order.orderDetails
        }

        mainViewModel.favoriteRecipeList.observe(viewLifecycleOwner){
            favoriteReicpeList = it
        }
        // 전역변수 관리
        mainViewModel.nowISeeRecipe.observe(viewLifecycleOwner){
            if (it != null) {
                nowRecipeIdx = it
                Log.d(TAG, "onViewCreated: nowISeeRecipe: $it")
            }
        }
        mainViewModel.recipeSteps.observe(viewLifecycleOwner){
            if (it != null) {
                recipeSteps.clear()
                for(item in it){
                    recipeSteps.add(item.instruction)
                }
                Log.d(TAG, "onViewCreated: recipeSteps: $it")
            }
        }
        mainViewModel.isDataReady.observe(viewLifecycleOwner){
            if(it){
                selectedRecipeList = mainViewModel.selectedRecipeList.value!!
                totalRecipes = selectedRecipeList.distinctBy { it.recipeName }.count()

                // 공통 이벤트 처리
                initEvent()
                // viewpager 처리
                initViewPager()
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
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // 화면 회전 잠금
    }
    override fun onPause() {
        super.onPause()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
    ////////////////////////////////////////////////////////////////////////////////////////
    override fun onDestroyView() {
        super.onDestroyView()
        _fullRecipeBinding = null
        _fullRecipeItemBinding = null
    }

    fun initEvent(){
        // slidepannel이 다 펴질 때만 scroll 가능하게!
        slidingUpPanelLayout = fullRecipeItemBinding.fullrecipeFmSlideuppanel // XML의 SlidingUpPanelLayout id
        scrollView = fullRecipeItemBinding.scrollView // XML의 NestedScrollView id
        scrollView.isScrollable = false

        // ViewPager2 제스처 처리 추가
        val viewPager = fullRecipeBinding.fullrecipeFmVp
        
        // ViewPager의 터치 이벤트를 직접 제어
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                // 패널이 열려있을 때는 스크롤 무시
                if (slidingUpPanelLayout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    viewPager.isUserInputEnabled = false
                }
            }
        })

        slidingUpPanelLayout.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View, slideOffset: Float) {
                scrollView.isScrollable = slideOffset == 1.0f
                // 패널이 움직이는 동안에는 ViewPager 스와이프 비활성화
                viewPager.isUserInputEnabled = slideOffset == 0f
            }
            
            override fun onPanelStateChanged(panel: View, previousState: SlidingUpPanelLayout.PanelState, newState: SlidingUpPanelLayout.PanelState) {
                when (newState) {
                    SlidingUpPanelLayout.PanelState.EXPANDED -> {
                        // 패널이 완전히 열렸을 때
                        viewPager.isUserInputEnabled = false
                    }
                    SlidingUpPanelLayout.PanelState.COLLAPSED -> {
                        // 패널이 완전히 닫혔을 때
                        viewPager.isUserInputEnabled = true
                    }
                    else -> {
                        // 드래그 중에는 ViewPager 비활성화
                        viewPager.isUserInputEnabled = false
                    }
                }
            }
        })

        // SlidingUpPanel의 드래그 영역 설정
        slidingUpPanelLayout.setDragView(fullRecipeItemBinding.fullrecipeFmSlideLayout)

        // 돌아가기 버튼을 누르면 이전 Fragment로 돌아감.
        fullRecipeBinding.fullrecipeFmBtnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
    fun initViewPager() {
        var customList:MutableList<String> = mutableListOf()
        var forAdapterSelectedRecipeList :MutableList<Recipe> = mutableListOf()
        var recipeCount = 0
        if(whereAmICame == 1){
            recipeCount = selectedRecipeList.count()
            forAdapterSelectedRecipeList = mutableListOf(selectedRecipeList.first())
        }
        else if(whereAmICame == 2){
            customList.clear()
            for(item in orderDetails){
                customList.add(item.customerRequest)
            }
            forAdapterSelectedRecipeList = selectedRecipeList
        }
        viewPagerAdapter = FullRecipeViewPagerAdapter(
            recipeList = forAdapterSelectedRecipeList,
            whereAmICame = whereAmICame,
            customList = customList,
            favoriteRecipeList = favoriteReicpeList,
            recipeCount = recipeCount,
            email = mainViewModel.userInfo.value!!.email,
            itemClickListener = object : FullRecipeViewPagerAdapter.ItemClickListener {
                override fun onHeartClick(recipeName: String, isFavorite: Boolean) {
                    if (!isFavorite) {
                        for(item in mainViewModel.recipeList.value!!.filter { it.recipeName == recipeName }){
                            favoriteReicpeList.add(FavoriteRecipe(
                                recipeId = item.recipeId,
                                recipeName = item.recipeName
                            ))
                            viewModel.likeRecipe(ApplicationClass.sharedPreferencesUtil.getUser().userId!!.toInt(), item.recipeId)
                        }
                        mainViewModel.setLikeRecipes(favoriteReicpeList)
                    } else {
                        favoriteReicpeList.removeAll { it.recipeName == recipeName }
                        mainViewModel.setLikeRecipes(favoriteReicpeList)
                        for(item in mainViewModel.recipeList.value!!.filter { it.recipeName == recipeName }){
                            viewModel.unLikeRecipe(ApplicationClass.sharedPreferencesUtil.getUser().userId!!.toInt(), item.recipeId)
                        }
                    }
                    viewPagerAdapter.favoriteRecipeList = favoriteReicpeList
                    viewPagerAdapter.notifyDataSetChanged()
                }

                override fun onHotIceClick(recipeName: String, type: String) {
                    val iceRecipe = selectedRecipeList.filter { it.type.equals("ICE") }.first()
                    val hotRecipe = selectedRecipeList.filter { it.type.equals("HOT") }.first()
                    if(type.equals("ICE")){
                        viewPagerAdapter.recipeList = mutableListOf(iceRecipe)
                    } else if(type.equals("HOT")){
                        viewPagerAdapter.recipeList = mutableListOf(hotRecipe)
                    }
                    viewPagerAdapter.notifyDataSetChanged()
                }

                override fun onRecipeStepClick(recipe: Recipe, nowISeeStep: Int) {
                    if(whereAmICame == 1){
                        val bundle = Bundle().apply {
                            putInt("whereAmICame", 3)
                        }
                        mainViewModel.setSelectedRecipeGoToStepRecipe(mutableListOf(recipe), nowISeeStep)
                        findNavController().navigate(R.id.stepRecipeFragment, bundle)
                    }
                    else if(whereAmICame == 2){
                        val bundle = Bundle().apply {
                            putInt("whereAmICame", 4)
                        }
                        mainViewModel.setSelectedRecipeGoToStepRecipeOnOrder(recipe, nowISeeStep)
                        findNavController().navigate(R.id.stepRecipeFragment, bundle)
                    }
                }
            },
        )

        fullRecipeBinding.fullrecipeFmVp.apply {
            adapter = viewPagerAdapter
            setCurrentItem(mainViewModel.nowISeeRecipe.value ?: 0, false)
        }
    }

}