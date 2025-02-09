package com.ssafy.reper.ui.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.reper.base.ApplicationClass
import com.ssafy.reper.data.dto.Order
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.data.remote.RetrofitUtil
import com.ssafy.reper.data.remote.RetrofitUtil.Companion.orderService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                list = mutableListOf()
            }
            _recipeList.value = list
        }
    }


    suspend fun searchRecipeName(storeId: Int, name: String): MutableList<Recipe> {
        return withContext(Dispatchers.IO) { // 백그라운드 스레드에서 실행
            try {
                recipeService.searchRecipeName(storeId, name)
            } catch (e: Exception) {
                mutableListOf() // 예외 발생 시 빈 리스트 반환
            }
        }
    }
}