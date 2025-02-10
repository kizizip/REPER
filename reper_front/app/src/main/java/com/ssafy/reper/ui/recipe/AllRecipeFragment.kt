package com.ssafy.reper.ui.recipe

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.GridLayout
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.R
import com.ssafy.reper.base.ApplicationClass
import com.ssafy.reper.databinding.FragmentAllRecipeBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.login.LoginActivity
import com.ssafy.reper.ui.order.OrderViewModel
import com.ssafy.smartstore_jetpack.util.CommonUtils.makeComma
import kotlinx.coroutines.launch

private const val TAG = "AllRecipeFragment_정언"
class AllRecipeFragment : Fragment() {
    ////////////////////////////////////
    // - 라디오 그룹으로 필터 다시 만들기.
    // - 재료 포함 제외 검색, 유사도 되는 건지 묻기.
    //      1. 유사도 X -> 샷 과 같은 명확한 것으로 필터 변경
    //      2. 유사도 O -> 현상 유지
    // - 카테고리누르고 필터나 검색 눌렀을 때 rv 업데이트 되게 로직 변경 (현재: 카테고리를 누르면 rv 업데이트. ViewModel.recipeList가 업데이트 되면 initAdapter 호출 됨.!!!)
    // - 검색 구현
    // - 검색 버튼이 꼭 필요한가? 필터에서 하나 클릭하면 다이얼로그 꺼지면서 바로 검색되게 할 거임.
    // - 즐겨찾기 구현
    ////////////////////////////////////


    var category : MutableList<String> = mutableListOf()

    private val viewModel: RecipeViewModel by viewModels()

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
        // 이벤트 관리
        initEvent()
        // RecyclerView adapter 처리
        initAdapter()
    }
    override fun onResume() {
        super.onResume()
        mainActivity.showBottomNavigation()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _allRecipeBinding = null
    }

    fun initEvent(){
        // 탭 초기 상태 설정 (단계별 레시피 선택)
        allRecipeBinding.allrecipeFmStepRecipeTab.isSelected = true
        allRecipeBinding.allrecipeFmFullRecipeTab.isSelected = false

        // 탭 클릭 리스너 설정
        allRecipeBinding.allrecipeFmStepRecipeTab.setOnClickListener {
            allRecipeBinding.allrecipeFmStepRecipeTab.isSelected = true
            allRecipeBinding.allrecipeFmFullRecipeTab.isSelected = false
        }

        allRecipeBinding.allrecipeFmFullRecipeTab.setOnClickListener {
            allRecipeBinding.allrecipeFmStepRecipeTab.isSelected = false
            allRecipeBinding.allrecipeFmFullRecipeTab.isSelected = true
        }

        allRecipeBinding.allrecipeFmBtnFilter.setOnClickListener {
            var howSearch = 0

            val dialog = Dialog(mainActivity)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_ingredient_filter)
            dialog.findViewById<View>(R.id.dialog_filter_cancel_btn).setOnClickListener {
                dialog.dismiss()
            }

            val filterSpinner = dialog.findViewById<Spinner>(R.id.filter_spinner)
            val filterSpinnerOptions = listOf("포함 검색", "제외 검색")
            val adapter = ArrayAdapter(mainActivity, R.layout.item_filter_spinner, filterSpinnerOptions)
            filterSpinner.adapter = adapter
            filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedItem = filterSpinnerOptions[position]
                    if(selectedItem.equals("포함 검색")){
                        howSearch = 0
                    }
                    else if(selectedItem.equals("제외 검색")){
                        howSearch = 1
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

            // RadioButton 리스트 가져오기
            val radioButtons = listOf(
                dialog.findViewById<RadioButton>(R.id.radio_coffee),
                dialog.findViewById<RadioButton>(R.id.radio_chocolate),
                dialog.findViewById<RadioButton>(R.id.radio_milk),
                dialog.findViewById<RadioButton>(R.id.radio_cream),
                dialog.findViewById<RadioButton>(R.id.radio_strawberry),
                dialog.findViewById<RadioButton>(R.id.radio_lemon),
                dialog.findViewById<RadioButton>(R.id.radio_blueberry),
                dialog.findViewById<RadioButton>(R.id.radio_grapefruit)
            )

            // 하나만 선택되도록 설정
            for(radio in radioButtons) {
                radio.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        allRecipeBinding.allrecipeFmEtSearch.setText(radio.text)
                        for (otherRadio in radioButtons) {
                            if (otherRadio != radio) {
                                otherRadio.isChecked = false
                            }
                        }
                        if(howSearch == 0){
                            viewModel.searchRecipeIngredientInclude(
                                ApplicationClass.sharedPreferencesUtil.getStoreId(),
                                allRecipeBinding.allrecipeFmEtSearch.text.toString())
                        }
                        else if(howSearch == 1){
                            viewModel.searchRecipeIngredientExclude(
                                ApplicationClass.sharedPreferencesUtil.getStoreId(),
                                allRecipeBinding.allrecipeFmEtSearch.text.toString())
                        }
                        dialog.dismiss()
                    }
                }
            }

            dialog.show()
        }

        allRecipeBinding.allrecipeFmEtSearch.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                view?.windowToken?.let {
                    imm.hideSoftInputFromWindow(it, 0)
                }
                true // 이벤트 처리 완료
            } else {
                false // 기본 동작 유지
            }
        }
        allRecipeBinding.allrecipeFmEtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // text 공백 및 줄바꿈 제거
                val inputText = s?.trim().toString()
                if(inputText.isBlank()){
                    viewModel.getRecipes(ApplicationClass.sharedPreferencesUtil.getStoreId())
                }
                else{
                    viewModel.searchRecipeName(ApplicationClass.sharedPreferencesUtil.getStoreId(), inputText)
                }
            }
        })
    }
    fun initAdapter() {
        // allrecipe item 클릭 이벤트 리스너
        allRecipeListAdapter = AllRecipeListAdapter(mutableListOf()) { id, recipeName ->
            // 즐겨찾기 버튼을 눌렀을 때
            if(id == 0){

            }
            // 아이템을 눌렀을 때
            else if(id == 1){
                lifecycleScope.launch {
                    val icehotList= viewModel.recipeList.value!!.filter { it.recipeName == recipeName }

                    if(allRecipeBinding.allrecipeFmFullRecipeTab.isSelected == true){
                        // 클릭 이벤트 -> recipeId 전달
                        val bundle = Bundle().apply {
                            putIntegerArrayList("idList", icehotList.map { it.recipeId }.toCollection(ArrayList()))
                        }
                        findNavController().navigate(R.id.fullRecipeFragment, bundle)

                    }
                    else if(allRecipeBinding.allrecipeFmStepRecipeTab.isSelected == true){
                        var ice = -1
                        var hot = -1
                        for(item in icehotList){
                            if(item.type.equals("ICE")){
                                ice = item.recipeId
                            }
                            else if(item.type.equals("HOT")){
                                hot = item.recipeId
                            }
                        }

                        if(ice != -1 && hot != -1){
                            val dialog = Dialog(mainActivity)
                            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            dialog.setContentView(R.layout.dialog_icehot)

                            dialog.findViewById<CardView>(R.id.icehot_d_btn_hot).visibility = View.GONE
                            dialog.findViewById<CardView>(R.id.icehot_d_btn_ice).visibility = View.GONE
                            if(ice != -1){
                                dialog.findViewById<CardView>(R.id.icehot_d_btn_ice).visibility = View.VISIBLE
                            }
                            if(hot != -1){
                                dialog.findViewById<CardView>(R.id.icehot_d_btn_hot).visibility = View.VISIBLE
                            }
                            dialog.findViewById<CardView>(R.id.icehot_d_btn_hot).setOnClickListener {
                                // 클릭 이벤트 -> recipeId 전달
                                val bundle = Bundle().apply {
                                    putInt("whereAmICame", 1)
                                    putIntegerArrayList("recipeIdList", arrayListOf(hot))
                                }
                                findNavController().navigate(R.id.stepRecipeFragment, bundle)
                                dialog.dismiss()
                            }
                            dialog.findViewById<CardView>(R.id.icehot_d_btn_ice).setOnClickListener {
                                // 클릭 이벤트 -> recipeId 전달
                                val bundle = Bundle().apply {
                                    putInt("whereAmICame", 1)
                                    putIntegerArrayList("recipeIdList", arrayListOf(ice))
                                }
                                findNavController().navigate(R.id.stepRecipeFragment, bundle)
                                dialog.dismiss()
                            }
                            dialog.show()
                        }
                        else if (ice != -1){
                            // 클릭 이벤트 -> recipeId 전달
                            val bundle = Bundle().apply {
                                putInt("whereAmICame", 1)
                                putIntegerArrayList("recipeIdList", arrayListOf(ice))
                            }
                            findNavController().navigate(R.id.stepRecipeFragment, bundle)
                        }
                        else if (hot != -1){
                            // 클릭 이벤트 -> recipeId 전달
                            val bundle = Bundle().apply {
                                putInt("whereAmICame", 1)
                                putIntegerArrayList("recipeIdList", arrayListOf(hot))
                            }
                            findNavController().navigate(R.id.stepRecipeFragment, bundle)
                        }
                    }
                }
            }
        }

        allRecipeBinding.allrecipeFmRv.apply {
            addItemDecoration(GridSpacingItemDecoration(2, 10)) // 2열, 20dp 간격
            layoutManager = GridLayoutManager(mainActivity, 2)

            viewModel.getRecipes(ApplicationClass.sharedPreferencesUtil.getStoreId())

            viewModel.recipeList.observe(viewLifecycleOwner){
                if(it.isEmpty()){
                    allRecipeBinding.allrecipeFmRv.visibility = View.GONE
                    allRecipeBinding.allrecipeFmTvNorecipe.visibility = View.VISIBLE
                }
                else{
                    allRecipeBinding.allrecipeFmRv.visibility = View.VISIBLE
                    allRecipeBinding.allrecipeFmTvNorecipe.visibility = View.GONE

                    allRecipeListAdapter.recipeList = it.distinctBy { it.recipeName }.toMutableList()
                    adapter = allRecipeListAdapter
                    category.clear()
                    category.add("카테고리")
                    for(recipe in it){
                        if(!category.contains(recipe.category)){
                            category.add(recipe.category)
                        }
                    }
                    initSpinner()
                }
            }
        }
    }
    fun initSpinner(){
        val myAdapter = ArrayAdapter(mainActivity, R.layout.item_allrecipe_spinner, category)

        allRecipeBinding.allrecipeFmSp.adapter = myAdapter

        val defaultPosition = category.indexOf("카테고리")
        if (defaultPosition != -1) {
            allRecipeBinding.allrecipeFmSp.setSelection(defaultPosition)
        }

        allRecipeBinding.allrecipeFmSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if(position == defaultPosition){
                    allRecipeListAdapter.recipeList = viewModel.recipeList.value!!.distinctBy { it.recipeName }.toMutableList()
                    allRecipeListAdapter.notifyDataSetChanged()
                }
                else{
                    allRecipeListAdapter.recipeList = viewModel.recipeList.value!!.distinctBy { it.recipeName }.filter { it.category == category[position] }.toMutableList()
                    allRecipeListAdapter.notifyDataSetChanged()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    // recyclerview Grid 형태 여백 주는 Class (Deco 용임)
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