package com.ssafy.reper.ui.boss

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.reper.data.dto.Employee
import com.ssafy.reper.data.dto.Notice
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.data.dto.RequestStore
import com.ssafy.reper.data.dto.Store
import com.ssafy.reper.data.remote.RetrofitUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import kotlin.math.log

private const val TAG = "BossViewModel"

class BossViewModel : ViewModel() {

    //스토어 정보 리스트
    private val _myStoreList = MutableLiveData<List<Store>>()
    val myStoreList: MutableLiveData<List<Store>> get() = _myStoreList

    fun setMyStoreList(list: List<Store>) {
        _myStoreList.value = list
    }

    //레시피 정보 리스트
    private val _recipeList = MutableLiveData<List<Recipe>>()
    val recipeList: MutableLiveData<List<Recipe>> get() = _recipeList

    fun setRecipeList(list: List<Recipe>) {
        _recipeList.value = list
        Log.d(TAG, "setRecipeList: $list")
    }

    //승인된 직원 리스트
    private val _accessList = MutableLiveData<List<Employee>>()
    val accessList: MutableLiveData<List<Employee>> get() = _accessList
    fun getAccessEmployeeList(employees: List<Employee>){
        _accessList.value = employees
    }

    //승인 대기중인 목록
    private val _waitingList = MutableLiveData<List<Employee>>()
    val waitingList: LiveData<List<Employee>> get() = _waitingList

    fun getWaitingEmployee(employees: List<Employee>) {
        _waitingList.value = employees
    }



    fun getAllEmployee(storeId: Int) {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.storeService.allEmployee(storeId)
            }.onSuccess { employees ->
                val access = employees.filter { it.employed }  // employed가 true인 직원들
                val waiting = employees.filter { !it.employed } // employed가 false인 직원들
                getAccessEmployeeList(access)
                getWaitingEmployee(waiting)
                Log.d(TAG, "Access: $access")
                Log.d(TAG, "Waiting: $waiting")
            }.onFailure {
                Log.d(TAG, "Error: ${it.message}")
            }
        }
    }

    fun deleteEmployee(storeId: Int,userId: Int){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.storeEmployeeService.deleteEmployee(storeId, userId)
            }.onSuccess {
                Log.d(TAG, "deleteEmployee: 성공")
                getAllEmployee(storeId)
            }.onFailure {
                Log.d(TAG, "deleteEmployee: ${it.message}")
            }
        }
    }

    fun acceptEmployee(storeId: Int,userId: Int){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.storeEmployeeService.acceptEmployee(storeId, userId)
            }.onSuccess {
                Log.d(TAG, "acceptEmployee: 성공")
                getAllEmployee(storeId)
            }.onFailure {
                Log.d(TAG, "acceptEmployee: ${it.message}")
            }
        }
    }


    fun getStoreList(userId: Int) {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.storeService.findBossStore(userId)
            }.onSuccess {
                setMyStoreList(it)
                Log.d(TAG, "getStoreList: ${it}")
            }.onFailure {
                Log.d(TAG, "getStoreList: ${it.message}")
            }
        }
    }


    fun addStore(storeName: String, userId: Int) {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.storeService.addStore(RequestStore(userId, storeName))
            }.onSuccess {
                getStoreList(userId)
            }.onFailure {
                Log.d(TAG, "addStore: ${it.message}")
            }
        }
    }

    fun deleteStore(storeId: Int, userId: Int) {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.storeService.deleteStore(storeId)
            }.onSuccess {
                Log.d(TAG, "deleteStore: $it")
                getStoreList(userId)
                _myStoreList.postValue(_myStoreList.value) // 강제로 LiveData 갱신
            }.onFailure {
                Log.d(TAG, "deleteStore: ${it.message}")
            }
        }

    }

    fun uploadRecipe(storeId: Int, recipeFile: MultipartBody.Part) {
        Log.d(TAG, "uploadRecipe: 파일명=${recipeFile.headers}")
        val requestBody = recipeFile.body
        Log.d(TAG, "uploadRecipe: 파일 RequestBody 크기=${requestBody?.contentLength()} bytes")

        viewModelScope.launch {
            runCatching {
                val response = RetrofitUtil.recipeService.recipeUpload(storeId, recipeFile)
                Log.d(TAG, "uploadRecipe: 서버 응답 = ${response}")
            }.onSuccess {
                Log.d(TAG, "uploadRecipe: 성공")
                getMenuList(storeId )
            }.onFailure {
                Log.d(TAG, "uploadRecipe: 실패 - ${it.message}")
            }
        }
    }


    fun getMenuList(storeId: Int) {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.recipeService.getStoreRecipe(storeId)
            }.onSuccess {
                setRecipeList(it)
                Log.d(TAG, "getMenuList: $it")
            }.onFailure {
                Log.d(TAG, "getMenuList: ${it.message}")
            }
        }
    }

    fun deleteRecipe(recipeId: Int, storeId: Int):String {
        var message = ""
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.recipeService.recipeDelete(recipeId)
            }.onSuccess {
                getMenuList(storeId)
                message =  "레시피 삭제 성공"
            }.onFailure {
                message =  "레시피 삭제실패"
                Log.d(TAG, "deleteRecipe: ${it}")
            }
        }
        return message
    }


    fun searchRecipe(storeId: Int, recipeName: String){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.recipeService.searchRecipe(storeId, recipeName)
            }.onSuccess {
                setRecipeList(it)
            }.onFailure {
                Log.d(TAG, "searchRecipe: ${it.message}")
            }
        }
    }

}