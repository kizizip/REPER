package com.ssafy.reper.ui.boss

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssafy.reper.data.dto.Employee
import com.ssafy.reper.data.dto.StoreNoticeResponse

private const val TAG = "BossViewModel"

class BossViewModel: ViewModel() {

    //스토어 정보 리스트
    private val _myStoreList = MutableLiveData<List<StoreNoticeResponse>>()
    val myStoreList: MutableLiveData<List<StoreNoticeResponse>> get() = _myStoreList

    //승인된 직원 리스트
    private val _accessList = MutableLiveData<List<Employee>>()
    val accessList: MutableLiveData<List<Employee>> get() = _accessList

    //스토어 정보 리스트
    private val _waitingList = MutableLiveData<List<Employee>>()
    val waitingList: MutableLiveData<List<Employee>> get() = _waitingList


    fun getAllEmployee(storeId: Int){
        
    }






}