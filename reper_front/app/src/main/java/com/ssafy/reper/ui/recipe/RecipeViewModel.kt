package com.ssafy.reper.ui.recipe

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.common.KakaoSdk.type
import com.ssafy.reper.base.ApplicationClass
import com.ssafy.reper.data.dto.FavoriteRecipe
import com.ssafy.reper.data.dto.Ingredient
import com.ssafy.reper.data.dto.Order
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.data.dto.RecipeResponse
import com.ssafy.reper.data.remote.RetrofitUtil
import com.ssafy.reper.data.remote.RetrofitUtil.Companion.orderService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "RecipeViewModel_정언"
class RecipeViewModel : ViewModel() {
    private val recipeService = RetrofitUtil.recipeService

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

//    fun searchRecipeName(storeId: Int, name: String) {
//        viewModelScope.launch {
//            try {
//                Log.d(TAG, "🔍 API 요청 - storeId: $storeId, name: $name")
//
//                val response = recipeService.searchRecipeName(storeId, name)
//
//                if (response.isSuccessful) {
//                    val list = response.body() ?: emptyList()
//                    Log.d(TAG, "searchRecipeName: ${list}")
//                    val result = list.map { recipeService.getRecipe(it.recipeId) }
//                    _recipeList.value = result.toMutableList()
//                    Log.d(TAG, "✅ 성공: ${_recipeList.value}")
//                } else {
//                    // ❌ 서버에서 응답이 왔지만 404 또는 다른 오류
//                    val errorBody = response.errorBody()?.string()
//                    Log.e(TAG, "❌ HTTP ${response.code()} - ${response.message()} \n 🔍 서버 응답: $errorBody")
//                    _recipeList.value = mutableListOf()
//                }
//            } catch (e: HttpException) {
//                // ❌ Retrofit의 HTTP 예외 (서버 응답 실패)
//                Log.e(TAG, "⚠️ HttpException: HTTP ${e.code()} - ${e.message()} \n ${e.response()?.errorBody()?.string()}")
//                _recipeList.value = mutableListOf()
//            } catch (e: IOException) {
//                // ❌ 네트워크 오류 (인터넷 끊김, 서버 다운 등)
//                Log.e(TAG, "🚨 네트워크 오류: ${e.localizedMessage}", e)
//                _recipeList.value = mutableListOf()
//            } catch (e: Exception) {
//                // ❌ 기타 예외 (JSON 파싱 오류 등)
//                Log.e(TAG, "💥 알 수 없는 예외 발생: ${e.localizedMessage}", e)
//                _recipeList.value = mutableListOf()
//            }
//        }
//    }



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

    private val _favoriteRecipeList =
        MutableLiveData<MutableList<FavoriteRecipe>>()
    val favoriteRecipeList: LiveData<MutableList<FavoriteRecipe>>
        get() = _favoriteRecipeList

    fun getLikeRecipes(storeId:Int, userId:Int){
        viewModelScope.launch {
            var list:MutableList<FavoriteRecipe>
            try {
                list = recipeService.getLikeRecipes(storeId, userId)

            }
            catch (e: HttpException){
                Log.d(TAG, "getLikeRecipes :error: ${e.response()?.errorBody().toString()}")
                list = mutableListOf()
            }
            _favoriteRecipeList.value = list
        }
    }

    fun likeRecipe(userId:Int, recipeId:Int){
        viewModelScope.launch {
            try {
                recipeService.likeRecipe(userId, recipeId)
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