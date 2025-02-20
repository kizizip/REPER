package com.ssafy.reper.ui.recipe

import MainActivityViewModel
import android.app.Dialog
import android.content.Context
import android.content.pm.ActivityInfo
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.R
import com.ssafy.reper.base.ApplicationClass
import com.ssafy.reper.data.dto.FavoriteRecipe
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.databinding.FragmentAllRecipeBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.recipe.adapter.AllRecipeListAdapter
import com.ssafy.reper.util.ViewModelSingleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale.filter

private const val TAG = "AllRecipeFragment_정언"
class AllRecipeFragment : Fragment() {
    var category : MutableList<String> = mutableListOf()

    var favoriteReicpeList : MutableList<FavoriteRecipe> = mutableListOf()

    private val mainViewModel: MainActivityViewModel by lazy { ViewModelSingleton.mainActivityViewModel }
    private val viewModel: RecipeViewModel by viewModels()

    // 레시피 리스트 recyclerView Adapter
    private lateinit var allRecipeListAdapter: AllRecipeListAdapter

    private lateinit var mainActivity: MainActivity
    private var searchQuery = ""

    private var _allRecipeBinding : FragmentAllRecipeBinding? = null
    private val allRecipeBinding get() =_allRecipeBinding!!

    private var howSearch = 2  // 기본값은 2 (이름 검색)

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

        searchQuery = arguments?.getString("searchQuery").toString()

        mainViewModel.clearData()
        mainViewModel.isEmployee.observe(viewLifecycleOwner){
            if(it == true || mainViewModel.userInfo.value!!.role == "OWNER"){
                allRecipeBinding.allrecipeFmTvNorecipe.visibility = View.GONE

                allRecipeBinding.allrecipeFmRv.visibility = View.VISIBLE
                allRecipeBinding.allrecipeFmSp.visibility = View.VISIBLE
                allRecipeBinding.allrecipeFmEtSearch.isEnabled = true
                allRecipeBinding.allrecipeFmBtnFilter.isEnabled = true

                mainViewModel.getRecipeList()
                mainViewModel.recipeList.observe(viewLifecycleOwner){
                    viewModel.setAllRecipes()
                }
                // RecyclerView adapter 처리
                initAdapter()
            }
            else{
                Log.d(TAG, "isEmployee: ${it}")
                allRecipeBinding.allrecipeFmTvNorecipe.visibility = View.VISIBLE

                allRecipeBinding.allrecipeFmRv.visibility = View.GONE
                allRecipeBinding.allrecipeFmSp.visibility = View.GONE
                allRecipeBinding.allrecipeFmEtSearch.isEnabled = false
                allRecipeBinding.allrecipeFmBtnFilter.isEnabled = false
            }
        }

        favoriteReicpeList = mainViewModel.favoriteRecipeList.value ?: mutableListOf()
        // 이벤트 관리
        initEvent()
    }
    override fun onResume() {
        super.onResume()
        mainActivity.showBottomNavigation()
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // 화면 회전 잠금
        
        // 다른 프래그먼트에서 돌아올 때 검색어 초기화
        if (searchQuery == "" || searchQuery == "null") {
            allRecipeBinding.allrecipeFmEtSearch.setText("")
        }
        else{
            allRecipeBinding.searchLoadingLayout.visibility = View.VISIBLE
            allRecipeBinding.allrecipeFmEtSearch.setText(searchQuery)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        super.onDestroyView()
        _allRecipeBinding = null
    }

    fun initEvent(){
        // 검색 초기 상태 설정
        allRecipeBinding.allrecipeFmEtSearch.setText("")


        // 탭 초기 상태 설정 (단계별 레시피 선택)
        if(mainViewModel.nowTab.value == 1){ // 단계별
            allRecipeBinding.allrecipeFmStepRecipeTab.isSelected = true
            allRecipeBinding.allrecipeFmFullRecipeTab.isSelected = false
        }
        else{ // 전체
            allRecipeBinding.allrecipeFmStepRecipeTab.isSelected = false
            allRecipeBinding.allrecipeFmFullRecipeTab.isSelected = true
        }

        // 단계별 탭 클릭 리스너 설정
        allRecipeBinding.allrecipeFmStepRecipeTab.setOnClickListener {
            mainViewModel.setNowTab(1)
            allRecipeBinding.allrecipeFmStepRecipeTab.isSelected = true
            allRecipeBinding.allrecipeFmFullRecipeTab.isSelected = false
        }
        // 전체 탭 클릭 리스너 설정
        allRecipeBinding.allrecipeFmFullRecipeTab.setOnClickListener {
            mainViewModel.setNowTab(2)
            allRecipeBinding.allrecipeFmStepRecipeTab.isSelected = false
            allRecipeBinding.allrecipeFmFullRecipeTab.isSelected = true
        }

        var searchJob: Job? = null
        allRecipeBinding.allrecipeFmEtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    val searchText = s.toString()

                    if (searchText.isEmpty()) {
                        withContext(Dispatchers.Main) {
                            allRecipeBinding.searchLoadingLayout.visibility = View.GONE
                            viewModel.setAllRecipes()
                        }
                        return@launch
                    }

                    // 로딩 화면 표시
                    withContext(Dispatchers.Main) {
                        allRecipeBinding.searchLoadingLayout.visibility = View.VISIBLE
                    }

                    when (howSearch) {
                        0 -> viewModel.searchRecipeIngredientInclude(
                            ApplicationClass.sharedPreferencesUtil.getStoreId(),
                            searchText
                        )
                        1 -> viewModel.searchRecipeIngredientExclude(
                            ApplicationClass.sharedPreferencesUtil.getStoreId(),
                            searchText
                        )
                        else -> viewModel.searchRecipeName(
                            ApplicationClass.sharedPreferencesUtil.getStoreId(),
                            searchText
                        )
                    }

                    // 검색 완료 후 로딩 화면 숨기기
                    withContext(Dispatchers.Main) {
                        delay(800)
                        allRecipeBinding.searchLoadingLayout.visibility = View.GONE
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // viewModel의 recipeList 관찰
        viewModel.recipeList.observe(viewLifecycleOwner) {
            // 데이터가 업데이트되면 로딩 화면 숨기기
            allRecipeBinding.searchLoadingLayout.visibility = View.GONE
        }

        allRecipeBinding.allrecipeFmBtnFilter.setOnClickListener {
            val dialog = Dialog(mainActivity)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_ingredient_filter)
            dialog.findViewById<View>(R.id.dialog_filter_cancel_btn).setOnClickListener {
                howSearch = 2  // 다이얼로그 취소시 howSearch 초기화
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
    }
    fun initAdapter() {
        // allrecipe item 클릭 이벤트 리스너
        allRecipeListAdapter = AllRecipeListAdapter(mutableListOf(), mutableListOf()) { id, recipeName, recipeId, recipeImg ->
            // 즐겨찾기 버튼을 눌렀을 때
            if(id == 0){
                for(item in viewModel.recipeList.value!!.filter { it.recipeName == recipeName }){
                    favoriteReicpeList.add(FavoriteRecipe(
                        recipeId = item.recipeId,
                        recipeName = item.recipeName
                    ))
                    viewModel.likeRecipe(ApplicationClass.sharedPreferencesUtil.getUser().userId!!.toInt(), item.recipeId)
                }
                mainViewModel.setLikeRecipes(favoriteReicpeList)
            }
            // 즐겨찾기 제외 버튼을 눌렀을 떄
            else if(id == 1){
                favoriteReicpeList.removeAll { it.recipeName == recipeName }
                mainViewModel.setLikeRecipes(favoriteReicpeList)
                for(item in viewModel.recipeList.value!!.filter { it.recipeName == recipeName }){
                    viewModel.unLikeRecipe(ApplicationClass.sharedPreferencesUtil.getUser().userId!!.toInt(), item.recipeId)
                }
            }
            // 아이템을 눌렀을 때
            else if(id == 2){
                // mainViewModel에 기존에 있던 레시피 데이터 초기화
                mainViewModel.clearData()

                // 레시피 이름이 동일한 경우는 ICE HOT 일 거라 가정. -> 하지만..! 동일한 이름의 레시피 짱 많음..! => 가장 마지막으로 추가된 걸로...
                // 클릭한 레시피와 동일한 이름의 레시피를 모두 가져와서 ice와 hot을 구별
                val iceRecipe = viewModel.recipeList.value!!
                        .filter { it.recipeName == recipeName && it.type.equals("ICE") }
                        .maxByOrNull { it.recipeId }
                val hotRecipe = viewModel.recipeList.value!!
                        .filter { it.recipeName == recipeName && it.type.equals("HOT") }
                        .maxByOrNull { it.recipeId }
                val selectedRecipes = mutableListOf<Recipe>()
                iceRecipe?.let { selectedRecipes.add(it) }
                hotRecipe?.let { selectedRecipes.add(it) }

                // 전체 레시피 탭에서 단건 레시피를 클릭했을 떄
                if(allRecipeBinding.allrecipeFmFullRecipeTab.isSelected == true){
                    navigateToRecipeFragment(selectedRecipes, R.id.fullRecipeFragment)
                }
                // 단계별 레시피 탭에서 단건 레시피를 클릭했을 때
                else if(allRecipeBinding.allrecipeFmStepRecipeTab.isSelected == true){
                    // ICE와 HOT 둘 다 있을 때만 다이얼로그를 띄움
                    if(iceRecipe != null && hotRecipe != null) {
                        showIceHotDialog(selectedRecipes, iceRecipe, hotRecipe) { selectedRecipe ->
                            navigateToRecipeFragment(selectedRecipes, R.id.stepRecipeFragment)
                        }
                    } else {
                        // ICE나 HOT 중 하나만 있는 경우 바로 해당 레시피 선택
                        val recipe = iceRecipe ?: hotRecipe
                        if (recipe != null) {
                            navigateToRecipeFragment(selectedRecipes, R.id.stepRecipeFragment)
                        } else {
                            Log.e(TAG, "No recipes found for $recipeName")
                        }
                    }
                }
            }
        }

        viewModel.recipeList.observe(viewLifecycleOwner){
            allRecipeBinding.allrecipeFmRv.apply {
                if(itemDecorationCount == 0){
                    addItemDecoration(GridSpacingItemDecoration(2, 10)) // 2열, 20dp 간격
                }
                layoutManager = GridLayoutManager(mainActivity, 2)

                allRecipeBinding.allrecipeFmTvNorecipe.visibility = View.GONE

                if (viewModel.recipeList.value!!.isEmpty()) {
                    allRecipeBinding.allrecipeFmRv.visibility = View.GONE
                    allRecipeBinding.allrecipeFmTvNorecipe.visibility = View.VISIBLE
                    category.clear()
                    category.add("카테고리")
                    initSpinner()
                } else {
                    allRecipeBinding.allrecipeFmRv.visibility = View.VISIBLE
                    allRecipeBinding.allrecipeFmTvNorecipe.visibility = View.GONE

                    allRecipeListAdapter.recipeList =
                        viewModel.recipeList.value!!.distinctBy { it.recipeName }.toMutableList()

                    allRecipeListAdapter.favoriteRecipeList = mainViewModel.favoriteRecipeList.value!!
                    adapter = allRecipeListAdapter

                    category.clear()
                    category.add("카테고리")
                    for (recipe in viewModel.recipeList.value!!) {
                        if (!category.contains(recipe.category)) {
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
    fun navigateToRecipeFragment(list: MutableList<Recipe>, id :Int) {
        val bundle =Bundle().apply {
            putInt("whereAmICame", 1)
        }
        mainViewModel.setSelectedRecipes(list)
        findNavController().navigate(id, bundle)
    }
    fun showIceHotDialog(selectedRecipes: MutableList<Recipe>, iceRecipe: Recipe, hotRecipe: Recipe, onRecipeSelected: (MutableList<Recipe>) -> Unit) {
        val dialog = Dialog(mainActivity)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_icehot)

        // 다이얼로그 취소 리스너 설정
        dialog.setOnCancelListener {
            // isEmployee 상태 유지
            mainViewModel.getIsEmployee(ApplicationClass.sharedPreferencesUtil.getUser().userId!!.toInt())
        }

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