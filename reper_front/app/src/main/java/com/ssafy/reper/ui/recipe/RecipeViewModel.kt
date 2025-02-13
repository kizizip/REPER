package com.ssafy.reper.ui.recipe

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.data.dto.RecipeResponse
import com.ssafy.reper.data.remote.RetrofitUtil.Companion.recipeService
import kotlinx.coroutines.launch

private const val TAG = "RecipeViewModel_정언"
class RecipeViewModel : ViewModel() {

    private val _recipeList =
        MutableLiveData<MutableList<Recipe>>()
    val recipeList: LiveData<MutableList<Recipe>>
        get() = _recipeList

    fun getRecipes(storeId: Int){
        viewModelScope.launch {
            var list: MutableList<Recipe>
            try {
                list = recipeService.getAllRecipes(storeId)
            }
            catch (e:Exception){
                Log.d(TAG, "error: ${e}")
                list = mutableListOf()
            }
            _recipeList.value = list
        }
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
        viewModelScope.launch {
            var list:MutableList<RecipeResponse>
            var result:MutableList<Recipe> = mutableListOf()
            try {
                list = recipeService.searchRecipeInclude(storeId, ingredient)
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

    fun searchRecipeIngredientExclude(storeId:Int, ingredient:String){
        viewModelScope.launch {
            var list:MutableList<RecipeResponse>
            var result:MutableList<Recipe> = mutableListOf()
            try {
                list = recipeService.searchRecipeExclude(storeId, ingredient)
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
        var recipe = Recipe(
            category = "",
            ingredients = mutableListOf(),
            recipeId = recipeId,
            recipeImg = null,
            recipeName = "",
            recipeSteps = mutableListOf(),
            type = ""
        )
        viewModelScope.launch {
            try {
                recipe = recipeService.getRecipe(recipeId)
                Log.d(TAG, "getRecipe: ${recipe}")
            }
            catch (e:Exception){
                Log.d(TAG, "getRecipe : error: ${e.message.toString()}")
                recipe = Recipe(
                    category = "",
                    ingredients = mutableListOf(),
                    recipeId = recipeId,
                    recipeImg = null,
                    recipeName = "",
                    recipeSteps = mutableListOf(),
                    type = ""
                )
            }
        }
        _recipe.value = recipe
    }
}