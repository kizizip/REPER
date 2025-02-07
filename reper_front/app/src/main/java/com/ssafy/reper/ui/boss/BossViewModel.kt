package com.ssafy.reper.ui.boss

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.reper.data.dto.Employee
import com.ssafy.reper.data.dto.RequestStore
import com.ssafy.reper.data.dto.Store
import com.ssafy.reper.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

private const val TAG = "BossViewModel"

class BossViewModel: ViewModel() {

    //스토어 정보 리스트
    private val _myStoreList = MutableLiveData<List<Store>>()
    val myStoreList: MutableLiveData<List<Store>> get() = _myStoreList

    fun setMyStoreList(list: List<Store>) {
        _myStoreList.value = list
    }

    //승인된 직원 리스트
    private val _accessList = MutableLiveData<List<Employee>>()
    val accessList: MutableLiveData<List<Employee>> get() = _accessList

    //스토어 정보 리스트
    private val _waitingList = MutableLiveData<List<Employee>>()
    val waitingList: MutableLiveData<List<Employee>> get() = _waitingList


    fun getAllEmployee(storeId: Int){


    }



    fun getStoreList(userId: Int){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.bossService.findBossStore(userId)
            }.onSuccess {
                setMyStoreList(it)
                Log.d(TAG, "getStoreList: ${it}")
            }.onFailure {
                Log.d(TAG, "getStoreList: ${it.message}")
            }

        }

    }


    fun addStore(storeName: String, userId: Int){
        viewModelScope.launch {
         runCatching {
             RetrofitUtil.bossService.addStore(RequestStore(userId, storeName))
         }.onSuccess {
             getStoreList(userId)
         }.onFailure {
             Log.d(TAG, "addStore: ${it.message}")
         }
        }
    }

    fun deleteStore(storeId: Int, userId: Int){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.bossService.deleteEmployee(storeId)
            }.onSuccess {

            }.onFailure {
                Log.d(TAG, "deleteStore: ${it.message}")
            }
        }

    }






}