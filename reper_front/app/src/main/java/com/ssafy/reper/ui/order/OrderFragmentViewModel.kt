package com.ssafy.reper.ui.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.reper.base.ApplicationClass
import com.ssafy.reper.data.dto.Order
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.data.remote.RecipeService
import com.ssafy.reper.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

private const val TAG = "OrderFragmentViewModel_정언"
class OrderFragmentViewModel : ViewModel() {
    private val orderService = RetrofitUtil.orderService
    private val recipeService = RetrofitUtil.recipeService

    private val _orderList =
        MutableLiveData<MutableList<Order>>()
    val orderList: LiveData<MutableList<Order>>
        get() = _orderList

    private val _recipeNameList =
        MutableLiveData<MutableList<Recipe>>()
    val recipeNameList: LiveData<MutableList<Recipe>>
        get() = _recipeNameList

    fun getOrders() {
        viewModelScope.launch{
            var orderList:MutableList<Order>
            var recipeList:MutableList<Recipe> = mutableListOf()
            try{
                orderList = orderService.getAllOrder(ApplicationClass.sharedPreferencesUtil.getStoreId()).sortedByDescending { it.orderDate }.toMutableList()
                for (order in orderList){
                    recipeList.add(recipeService.getRecipe(order.orderDetails.first().recipeId))
                }

            }catch (e:Exception){
                orderList = mutableListOf()
                recipeList = mutableListOf()
            }

            _recipeNameList.value = recipeList
            _orderList.value = orderList
            
        }
    }
}