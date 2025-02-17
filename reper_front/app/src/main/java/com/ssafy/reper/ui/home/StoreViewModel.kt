package com.ssafy.reper.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.reper.data.dto.Store
import com.ssafy.reper.data.dto.StoreResponse
import com.ssafy.reper.data.dto.StoreResponseUser
import com.ssafy.reper.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

private const val TAG = "StoreViewModel_싸피"
class StoreViewModel : ViewModel() {

    //스토어 정보 리스트
    private val _myStoreList = MutableLiveData<List<StoreResponseUser>>()
    val myStoreList: MutableLiveData<List<StoreResponseUser>> get() = _myStoreList

    fun setMyStoreList(list: List<StoreResponseUser>) {
        _myStoreList.value = list
    }


    fun getUserStore(userId: Int) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "getUserStore: Starting request for userId=$userId")
                val response = RetrofitUtil.storeEmployeeService.getUserStore(userId)
                Log.d(TAG, "getUserStore: Response received, size=${response.size}")
                _myStoreList.postValue(response)
            } catch (e: Exception) {
                Log.e(TAG, "getUserStore error: ${e.message}")
                _myStoreList.postValue(emptyList())
            }
        }
    }

    suspend fun getStore(storeId: Int): StoreResponse? {
        return try {
            RetrofitUtil.storeService.getStore(storeId)
        } catch (e: Exception) {
            Log.d(TAG, "getStore error: ${e.message}")
            null
        }
    }

    fun deleteStore(storeId: Int, userId: Int){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.storeEmployeeService.deleteEmployee(storeId,userId)
            }.onSuccess {
                getUserStore(storeId)
                Log.d(TAG, "deleteStore: 삭제성공 ${myStoreList.value}")
            }.onFailure {
                Log.d(TAG, "deleteStore:${it.message} ")
            }
        }
    }

}