package com.ssafy.reper.ui.recipe

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.replace
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.R
import com.ssafy.reper.databinding.FragmentAllRecipeBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.smartstore_jetpack.util.CommonUtils.makeComma

private const val TAG = "AllRecipeFragment_정언"
class AllRecipeFragment : Fragment() {

    // 레시피 리스트 recyclerView Adapter
    private lateinit var allRecipeListAdapter: AllRecipeListAdapter

    private lateinit var mainActivity: MainActivity

    private var _allRecipeBinding : FragmentAllRecipeBinding? = null
    private val allRecipeBinding get() =_allRecipeBinding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _allRecipeBinding = FragmentAllRecipeBinding.inflate(inflater, container, false)
        return allRecipeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.showBottomNavigation()


        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        // 카테고리 spinner
        val category = arrayOf("coffee", "non-coffee")
        val myAdapter = ArrayAdapter(mainActivity, R.layout.item_allrecipe_spinner, category)

        allRecipeBinding.allrecipeFmSp.adapter = myAdapter

        allRecipeBinding.allrecipeFmSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (view == null) {
                    return
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////


        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        // 탭 초기 상태 설정 (단계별 레시피 선택)
        allRecipeBinding.allrecipeFmStepRecipeTab.isSelected = true
        allRecipeBinding.allrecipeFmFullRecipeTab.isSelected = false

        // 탭 클릭 리스너 설정
        allRecipeBinding.allrecipeFmStepRecipeTab.setOnClickListener {
            allRecipeBinding.allrecipeFmStepRecipeTab.isSelected = true
            allRecipeBinding.allrecipeFmFullRecipeTab.isSelected = false
            // 여기에 단계별 레시피 표시 로직 추가
        }

        allRecipeBinding.allrecipeFmFullRecipeTab.setOnClickListener {
            allRecipeBinding.allrecipeFmStepRecipeTab.isSelected = false
            allRecipeBinding.allrecipeFmFullRecipeTab.isSelected = true
            // 여기에 전체 레시피 표시 로직 추가
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        // RecyclerView adapter 처리
        initAdapter()
        ///////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _allRecipeBinding = null
    }

    fun initAdapter() {
        // allrecipe item 클릭 이벤트 리스너
        allRecipeListAdapter = AllRecipeListAdapter(mutableListOf()) { id, position ->
            // 즐겨찾기 버튼을 눌렀을 때
            if(id == 0){

            }
            // 아이템을 눌렀을 때
            else if(id == 1){
                if(allRecipeBinding.allrecipeFmFullRecipeTab.isSelected == true){
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.activityMainFragmentContainer, FullRecipeFragment())
                        .addToBackStack(null)
                        .commit()
                }
                else if(allRecipeBinding.allrecipeFmStepRecipeTab.isSelected == true){
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.activityMainFragmentContainer, StepRecipeFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        allRecipeBinding.allrecipeFmRv.apply {
            addItemDecoration(GridSpacingItemDecoration(2, 10)) // 2열, 20dp 간격
            layoutManager = GridLayoutManager(mainActivity, 2)
            allRecipeListAdapter.recipeList = mutableListOf("아메리카노", "카페라떼", "초코라떼", "민트초코프라프치노", "대추차", "레몬에이드", "서현이가 좋아하는 아이스티", "그녀는 신이야")
            adapter = allRecipeListAdapter
        }
    }

    class GridSpacingItemDecoration(private val spanCount: Int, private val spacing: Int) :
        RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount

            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount
        }
    }
}