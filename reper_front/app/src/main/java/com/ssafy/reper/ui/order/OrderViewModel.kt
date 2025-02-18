package com.ssafy.reper.ui.order

import MainActivityViewModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.reper.base.ApplicationClass
import com.ssafy.reper.data.dto.Order
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.data.remote.RetrofitUtil.Companion.recipeService
import com.ssafy.reper.data.remote.RetrofitUtil.Companion.orderService
import com.ssafy.reper.util.ViewModelSingleton
import kotlinx.coroutines.launch

private const val TAG = "OrderFragmentViewModel_정언"
class OrderViewModel : ViewModel() {

    private val mainViewModel: MainActivityViewModel by lazy { ViewModelSingleton.mainActivityViewModel }

    private val _orderList =
        MutableLiveData<MutableList<Order>>()
    val orderList: LiveData<MutableList<Order>>
        get() = _orderList

    fun getOrders() {
        viewModelScope.launch {
            try {
                val storeId = ApplicationClass.sharedPreferencesUtil.getStoreId()
                if (storeId != 0) {  // storeId가 유효한 경우에만 실행
                    Log.d(TAG, "getOrders: ${storeId}")
                    val thisOrderList = orderService.getAllOrder(storeId)
                        .sortedByDescending { it.orderDate }
                        .toMutableList()
                    _orderList.value = thisOrderList
                }
            } catch (e: Exception) {
                Log.e(TAG, "getOrders error: ${e.message}")
            }
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
                        list.add(mainViewModel.recipeList.value!!.filter { it.recipeId == detail.recipeId}.first())
                    }
                }
            }
            catch (e:Exception){
                item = Order(
                    completed = false,
                    orderDate = "",
                    orderDetails = mutableListOf(),
                    orderId = -1,
                    takeout = false
                )
            }
            _order.value = item
            _recipeList.value = list
        }
    }

    fun completeOrder(orderId: Int){
        viewModelScope.launch {
            try {
                orderService.orderComplete(orderId)
                // 주문 완료 후 데이터 새로고침
                getOrders()
                Log.d(TAG, "completeOrder: success orderId=$orderId")
            } catch (e: Exception) {
                Log.e(TAG, "completeOrder error: ${e.message}")
            }
        }
    }
}