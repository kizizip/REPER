package com.ssafy.reper.ui.recipe

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.ssafy.reper.R
import com.ssafy.reper.databinding.FragmentFullRecipeBinding
import com.ssafy.reper.databinding.FragmentStepRecipeBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.recipe.AllRecipeFragment.GridSpacingItemDecoration

private const val TAG = "FullRecipeFragment_정언"
class FullRecipeFragment : Fragment() {

    private lateinit var slidingUpPanelLayout: SlidingUpPanelLayout
    private lateinit var scrollView: LockableNestedScrollView

    // 레시피 리스트 recyclerView Adapter
    private lateinit var fullRecipeListAdapter: FullRecipeListAdapter

    private lateinit var mainActivity: MainActivity

    private var _fullRecipeBinding : FragmentFullRecipeBinding? = null
    private val fullRecipeBinding get() =_fullRecipeBinding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fullRecipeBinding = FragmentFullRecipeBinding.inflate(inflater, container, false)
        return fullRecipeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.hideBottomNavigation()

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        // 즐겨찾기 버튼이 눌리면
        fullRecipeBinding.fullrecipeFmBtnHeart.setOnClickListener {
            if(fullRecipeBinding.fullrecipeFmIvLineheart.visibility == View.VISIBLE){
                fullRecipeBinding.fullrecipeFmIvLineheart.visibility = View.GONE
                fullRecipeBinding.fullrecipeFmIvFullheart.visibility = View.VISIBLE
            }
            else{
                fullRecipeBinding.fullrecipeFmIvLineheart.visibility = View.VISIBLE
                fullRecipeBinding.fullrecipeFmIvFullheart.visibility = View.GONE
            }
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        // Ice Hot 버튼 클릭
        fullRecipeBinding.fullrecipeFmBtnIce.setOnClickListener {
            fullRecipeBinding.fullrecipeFmBtngroup.check(fullRecipeBinding.fullrecipeFmBtnIce.id)
            btnHotIceColorChange()
        }
        fullRecipeBinding.fullrecipeFmBtnHot.setOnClickListener {
            fullRecipeBinding.fullrecipeFmBtngroup.check(fullRecipeBinding.fullrecipeFmBtnHot .id)
            btnHotIceColorChange()
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        // RecyclerView adapter 처리
        initAdapter()
        ///////////////////////////////////////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        // slidepannel이 다 펴질 때만 scroll 가능하게!
        slidingUpPanelLayout = fullRecipeBinding.fullrecipeFmSlideuppanel // XML의 SlidingUpPanelLayout id
        scrollView = fullRecipeBinding.scrollView // XML의 NestedScrollView id
        scrollView.isScrollable = false
        slidingUpPanelLayout.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View, slideOffset: Float) {
                scrollView.isScrollable = slideOffset == 1.0f
            }

            override fun onPanelStateChanged(panel: View, previousState: SlidingUpPanelLayout.PanelState, newState: SlidingUpPanelLayout.PanelState) {
                // 상태 변화에 따른 추가 처리가 필요하면 여기에 작성
            }
        })
        ///////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fullRecipeBinding = null
    }

    fun btnHotIceColorChange(){
        if(fullRecipeBinding.fullrecipeFmBtngroup.checkedButtonId == fullRecipeBinding.fullrecipeFmBtnIce.id){
            fullRecipeBinding.fullrecipeFmBtnIce.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.green))
            fullRecipeBinding.fullrecipeFmBtnIce.setTextColor(ContextCompat.getColor(mainActivity, R.color.white))
            fullRecipeBinding.fullrecipeFmBtnHot.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.white))
            fullRecipeBinding.fullrecipeFmBtnHot.setTextColor(ContextCompat.getColor(mainActivity, R.color.green))
        }

        if(fullRecipeBinding.fullrecipeFmBtngroup.checkedButtonId == fullRecipeBinding.fullrecipeFmBtnHot.id){
            fullRecipeBinding.fullrecipeFmBtnIce.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.white))
            fullRecipeBinding.fullrecipeFmBtnIce.setTextColor(ContextCompat.getColor(mainActivity, R.color.green))
            fullRecipeBinding.fullrecipeFmBtnHot.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.green))
            fullRecipeBinding.fullrecipeFmBtnHot.setTextColor(ContextCompat.getColor(mainActivity, R.color.white))
        }
    }

    fun initAdapter() {
        // allrecipe item 클릭 이벤트 리스너
        fullRecipeListAdapter = FullRecipeListAdapter(mutableListOf()) { position ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.activityMainFragmentContainer, StepRecipeFragment())
                .addToBackStack(null)
                .commit()
        }

        fullRecipeBinding.fullrecipeFmRvRecipe.apply {
            layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            fullRecipeListAdapter.recipeStepList = mutableListOf("1. 샷을 내린다.", "2. 샷을 붓는다.", "3. 뜨거운 물을 넣는다.", "4.안녕하세요", "5. 저는 더미데이터 입니다.", "6. 더미", "7. 더미", "8. 더미", "9. 더미", "10. 더미", "11. 더미", "12. 더미", "13. 더미")
            adapter = fullRecipeListAdapter
        }
    }
}