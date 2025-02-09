package com.ssafy.reper.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.reper.base.ApplicationClass
import com.ssafy.reper.data.dto.Order
import com.ssafy.reper.data.dto.OrderDetail
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

private const val TAG = "OrderFragmentViewModel_정언"
class OrderViewModel : ViewModel() {
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

    private val _order =
        MutableLiveData<Order>()
    val order: LiveData<Order>
        get() = _order

    private val _recipeList =
        MutableLiveData<MutableList<Recipe>>()
    val recipeList: LiveData<MutableList<Recipe>>
        get() = _recipeList

    fun getOrder(orderId: Int){
        viewModelScope.launch {
            var item:Order
            var list:MutableList<Recipe> = mutableListOf()
            try {
                item = orderService.getOrder(ApplicationClass.sharedPreferencesUtil.getStoreId(), orderId)
                item.orderDetails?.let {
                    it.sortBy { it.recipeId }
                    for(detail in it){
                        list.add(recipeService.getRecipe(detail.recipeId))
                    }
                }
            }
            catch (e:Exception){
                item = Order(
                    completed = false,
                    orderDate = "",
                    orderDetails = mutableListOf(),
                    orderId = -1
                )
            }
            _order.value = item
            _recipeList.value = list
        }
    }
}