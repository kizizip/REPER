package com.ssafy.reper.ui.recipe

import MainActivityViewModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.data.dto.RecipeResponse
import com.ssafy.reper.data.remote.RetrofitUtil.Companion.recipeService
import com.ssafy.reper.util.ViewModelSingleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "RecipeViewModel_정언"
class RecipeViewModel : ViewModel() {

    private val mainViewModel: MainActivityViewModel by lazy { ViewModelSingleton.mainActivityViewModel }

    private val _recipeList = MutableLiveData<MutableList<Recipe>>(mutableListOf())
    val recipeList: LiveData<MutableList<Recipe>> = _recipeList

    fun setAllRecipes(){
        _recipeList.value = mainViewModel.recipeList.value
    }
    
    fun searchRecipeName(storeId: Int, name: String) {
        viewModelScope.launch {
            var list:MutableList<RecipeResponse>
            var result:MutableList<Recipe> = mutableListOf()
            try {
                list = recipeService.searchRecipeName(storeId, name)
                for(item in list){
                    result.add(recipeService.getRecipe(item.recipeId))
                }
            }

            catch (e:Exception){
                Log.d(TAG, "error: ${e}")
                result = mutableListOf()
            }
            _recipeList.value = result
        }
    }

    fun searchRecipeIngredientInclude(storeId:Int, ingredient:String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 검색 결과 받아오기
                val recipeResponses = recipeService.searchRecipeInclude(storeId, ingredient)
                
                // 병렬로 Recipe 정보 가져오기
                val recipes = recipeResponses.map { response ->
                    async { recipeService.getRecipe(response.recipeId) }
                }.awaitAll()

                // UI 업데이트
                withContext(Dispatchers.Main) {
                    _recipeList.value = recipes.toMutableList()
                }
            } catch (e: Exception) {
                Log.d(TAG, "error: ${e}")
                withContext(Dispatchers.Main) {
                    _recipeList.value = mutableListOf()
                }
            }
        }
    }

    fun searchRecipeIngredientExclude(storeId: Int, ingredient: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 검색 결과 받아오기
                val recipeResponses = recipeService.searchRecipeExclude(storeId, ingredient)
                
                // 병렬로 Recipe 정보 가져오기
                val recipes = recipeResponses.map { response ->
                    async { recipeService.getRecipe(response.recipeId) }
                }.awaitAll()

                // UI 업데이트
                withContext(Dispatchers.Main) {
                    _recipeList.value = recipes.toMutableList()
                }
            } catch (e: Exception) {
                Log.d(TAG, "error: ${e}")
                withContext(Dispatchers.Main) {
                    _recipeList.value = mutableListOf()
                }
            }
        }
    }

    fun likeRecipe(userId:Int, recipeId:Int){
        viewModelScope.launch {
            try {
                recipeService.likeRecipe(userId, recipeId)
                Log.d(TAG, "likeRecipe: ${recipeId}")
            }
            catch (e:Exception){
                Log.d(TAG, "likeRecipe :error: ${e}")
            }
        }
    }

    fun unLikeRecipe(userId:Int, recipeId:Int){
        viewModelScope.launch {
            try {
                recipeService.unLikeRecipe(userId, recipeId)
                Log.d(TAG, "unLikeRecipe: ${recipeId}")
            }
            catch (e:Exception){
                Log.d(TAG, "unLikeRecipe : error: ${e.message.toString()}")
            }
        }
    }

    private val _recipe =
        MutableLiveData<Recipe>()
    val recipe: LiveData<Recipe>
        get() = _recipe

    fun getRecipe(recipeId: Int){
        viewModelScope.launch {
            try {
                val recipe = recipeService.getRecipe(recipeId)
                _recipe.postValue(recipe)
            }
            catch (e:Exception){
                Log.d(TAG, "getRecipe : error: ${e.message.toString()}")
            }
        }
    }
}