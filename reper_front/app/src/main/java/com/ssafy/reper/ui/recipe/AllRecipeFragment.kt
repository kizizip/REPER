package com.ssafy.reper.ui.recipe

import MainActivityViewModel
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Spinner
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.R
import com.ssafy.reper.base.ApplicationClass
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.databinding.FragmentAllRecipeBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.recipe.adapter.AllRecipeListAdapter
import com.ssafy.reper.util.ViewModelSingleton

private const val TAG = "AllRecipeFragment_정언"
class AllRecipeFragment : Fragment() {
    var category : MutableList<String> = mutableListOf()

    private val mainViewModel: MainActivityViewModel by lazy { ViewModelSingleton.mainActivityViewModel }
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

        mainViewModel.clearData()
        mainViewModel.isEmployee.observe(viewLifecycleOwner){
            if(it == true){
                allRecipeBinding.allrecipeFmTvNorecipe.visibility = View.GONE

                allRecipeBinding.allrecipeFmRv.visibility = View.VISIBLE
                allRecipeBinding.allrecipeFmSp.visibility = View.VISIBLE
                allRecipeBinding.allrecipeFmEtSearch.isEnabled = true
                allRecipeBinding.allrecipeFmBtnFilter.isEnabled = true

                // RecyclerView adapter 처리
                initAdapter()
            }
            else{
                allRecipeBinding.allrecipeFmTvNorecipe.visibility = View.VISIBLE

                allRecipeBinding.allrecipeFmRv.visibility = View.GONE
                allRecipeBinding.allrecipeFmSp.visibility = View.GONE
                allRecipeBinding.allrecipeFmEtSearch.isEnabled = false
                allRecipeBinding.allrecipeFmBtnFilter.isEnabled = false
            }
        }
        mainViewModel.getIsEmployee(ApplicationClass.sharedPreferencesUtil.getUser().userId!!.toInt())
        // 이벤트 관리
        initEvent()
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
        // 검색 초기 상태 설정
        allRecipeBinding.allrecipeFmEtSearch.setText("")

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
                val imm = requireContext().
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
        allRecipeListAdapter = AllRecipeListAdapter(mutableListOf(), mutableListOf()) { id, recipeName, recipeId ->
            // 즐겨찾기 버튼을 눌렀을 때
            if(id == 0){
                viewModel.likeRecipe(ApplicationClass.sharedPreferencesUtil.getUser().userId!!.toInt(), recipeId)
            }
            // 즐겨찾기 제외 버튼을 눌렀을 떄
            else if(id == 1){
                viewModel.unLikeRecipe(ApplicationClass.sharedPreferencesUtil.getUser().userId!!.toInt(), recipeId)
            }
            // 아이템을 눌렀을 때
            else if(id == 2){
                // mainViewModel에 기존에 있던 레시피 데이터 초기화
                mainViewModel.clearData()

                // 레시피 이름이 동일한 경우는 ICE HOT 일 거라 가정. -> 하지만..! 동일한 이름의 레시피 짱 많음..! => 가장 마지막으로 추가된 걸로...
                // 클릭한 레시피와 동일한 이름의 레시피를 모두 가져와서 ice와 hot을 구별
                val iceRecipe =
                    viewModel.recipeList.value!!
                        .filter { it.recipeName == recipeName && it.type.equals("ICE") }
                        .maxByOrNull { it.recipeId }
                val hotRecipe =
                    viewModel.recipeList.value!!
                        .filter { it.recipeName == recipeName && it.type.equals("HOT") }
                        .maxByOrNull { it.recipeId }
                val selectedRecipes = mutableListOf<Recipe>()
                iceRecipe?.let { selectedRecipes.add(it) }
                hotRecipe?.let { selectedRecipes.add(it) }

                // 전체 레시피 탭에서 단건 레시피를 클릭했을 떄
                if(allRecipeBinding.allrecipeFmFullRecipeTab.isSelected == true){
                    navigateToRecipeFragment(selectedRecipes)
                }
                // 단계별 레시피 탭에서 단건 레시피를 클릭했을 때
                else if(allRecipeBinding.allrecipeFmStepRecipeTab.isSelected == true){
                    // ICE와 HOT 둘 다 있을 때만 다이얼로그를 띄움
                    if(iceRecipe != null && hotRecipe != null) {
                        showIceHotDialog(selectedRecipes, iceRecipe, hotRecipe) { selectedRecipe ->
                            navigateToRecipeFragment(selectedRecipes)
                        }
                    } else {
                        // ICE나 HOT 중 하나만 있는 경우 바로 해당 레시피 선택
                        val recipe = iceRecipe ?: hotRecipe
                        if (recipe != null) {
                            navigateToRecipeFragment(selectedRecipes)
                        } else {
                            Log.e(TAG, "No recipes found for $recipeName")
                        }
                    }
                }
            }
        }

        allRecipeBinding.allrecipeFmRv.apply {
            addItemDecoration(GridSpacingItemDecoration(2, 10)) // 2열, 20dp 간격
            layoutManager = GridLayoutManager(mainActivity, 2)

            allRecipeBinding.allrecipeFmTvNorecipe.visibility = View.GONE

            viewModel.getRecipes(ApplicationClass.sharedPreferencesUtil.getStoreId())
            mainViewModel.getLikeRecipes(
                ApplicationClass.sharedPreferencesUtil.getStoreId(),
                ApplicationClass.sharedPreferencesUtil.getUser().userId!!.toInt()
            )

            viewModel.recipeList.observe(viewLifecycleOwner) {
                if (it.isEmpty()) {
                    allRecipeBinding.allrecipeFmRv.visibility = View.GONE
                    allRecipeBinding.allrecipeFmTvNorecipe.visibility = View.VISIBLE
                    category.clear()
                    category.add("카테고리")
                    initSpinner()
                } else {
                    allRecipeBinding.allrecipeFmRv.visibility = View.VISIBLE
                    allRecipeBinding.allrecipeFmTvNorecipe.visibility = View.GONE

                    allRecipeListAdapter.recipeList =
                        it.distinctBy { it.recipeName }.toMutableList()
                    category.clear()
                    category.add("카테고리")
                    for (recipe in it) {
                        if (!category.contains(recipe.category)) {
                            category.add(recipe.category)
                        }
                    }
                    initSpinner()
                }
            }

            mainViewModel.favoriteRecipeList.observe(viewLifecycleOwner) {
                allRecipeListAdapter.favoriteRecipeList = it
                adapter = allRecipeListAdapter
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
    fun navigateToRecipeFragment(list: MutableList<Recipe>) {
        val bundle =Bundle().apply {
            putInt("whereAmICame", 1)
        }
        mainViewModel.setSelectedRecipes(list)
        findNavController().navigate(R.id.stepRecipeFragment, bundle)
    }
    fun showIceHotDialog(selectedRecipes: MutableList<Recipe>, iceRecipe: Recipe, hotRecipe: Recipe, onRecipeSelected: (MutableList<Recipe>) -> Unit) {
        val dialog = Dialog(mainActivity)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_icehot)

        dialog.findViewById<CardView>(R.id.icehot_d_btn_ice).visibility = View.VISIBLE
        dialog.findViewById<CardView>(R.id.icehot_d_btn_hot).visibility = View.VISIBLE

        // hot 선택 시
        dialog.findViewById<CardView>(R.id.icehot_d_btn_hot).setOnClickListener {
            selectedRecipes.remove(iceRecipe)
            dialog.dismiss()
            onRecipeSelected(selectedRecipes)
        }
        // ice 선택 시
        dialog.findViewById<CardView>(R.id.icehot_d_btn_ice).setOnClickListener {
            selectedRecipes.remove(hotRecipe)
            dialog.dismiss()
            onRecipeSelected(selectedRecipes)
        }
        dialog.show()
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