package com.ssafy.reper.ui.boss

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ssafy.reper.data.dto.Employee
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.data.dto.RequestStore
import com.ssafy.reper.data.dto.SearchedStore
import com.ssafy.reper.data.dto.Store
import com.ssafy.reper.data.local.SharedPreferencesUtil
import com.ssafy.reper.data.remote.RetrofitUtil
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

private const val TAG = "BossViewModel"

class BossViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferencesUtil = SharedPreferencesUtil(application.applicationContext)

    //스토어 정보 리스트
    private val _myStoreList = MutableLiveData<List<SearchedStore>>()
    val myStoreList: MutableLiveData<List<SearchedStore>> get() = _myStoreList

    fun setMyStoreList(list: List<SearchedStore>) {
        _myStoreList.value = list
    }

    //레시피 정보 리스트
    private val _recipeList = MutableLiveData<List<Recipe>>()
    val recipeList: MutableLiveData<List<Recipe>> get() = _recipeList

    fun setRecipeList(list: List<Recipe>) {
        _recipeList.value = list
        Log.d(TAG, "setRecipeList: $list")
    }

    //레시피 업로드 상태 관찰
    private val _recipeLoad = MutableLiveData<String?>()
    val recipeLoad: MutableLiveData<String?> get() = _recipeLoad

    fun setRecipeLoad(result: String?) {
        _recipeLoad.value = result
        Log.d(TAG, "setRecipeLoad: ${result}")
    }

    //승인된 직원 리스트
    private val _accessList = MutableLiveData<List<Employee>>()
    val accessList: MutableLiveData<List<Employee>> get() = _accessList
    fun getAccessEmployeeList(employees: List<Employee>) {
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
            try {
                val employees = RetrofitUtil.storeService.allEmployee(storeId)
                val access = employees.filter { it.employed }
                val waiting = employees.filter { !it.employed }
                
                _accessList.postValue(access)
                _waitingList.postValue(waiting)
                
                Log.d(TAG, "getAllEmployee: storeId=$storeId, access=${access.size}, waiting=${waiting.size}")
            } catch (e: Exception) {
                Log.e(TAG, "getAllEmployee error: ${e.message}")
                _accessList.postValue(emptyList())
                _waitingList.postValue(emptyList())
            }
        }
    }

    fun deleteEmployee(storeId: Int, userId: Int) {
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

    fun acceptEmployee(storeId: Int, userId: Int):String {
        var result : String = ""
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.storeEmployeeService.acceptEmployee(storeId, userId)
            }.onSuccess {
                Log.d(TAG, "acceptEmployee: 성공")
                getAllEmployee(storeId)
                result = "성공"
            }.onFailure {
                Log.d(TAG, "acceptEmployee: ${it.message}")
                result = "실패"
            }
        }
        return result
    }


    fun getStoreList(userId: Int) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "getStoreList: Starting request for userId=$userId")
                val response = RetrofitUtil.storeService.findBossStore(userId)
                Log.d(TAG, "getStoreList: Response received, size=${response.size}")
                _myStoreList.postValue(response)
            } catch (e: Exception) {
                Log.e(TAG, "getStoreList error: ${e.message}")
                _myStoreList.postValue(emptyList())
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
                Log.d(TAG, "Calling getStoreList(userId) after deletion")
                getStoreList(userId)
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
                sharedPreferencesUtil.addStateLoad("success")
                setRecipeLoad("success")
                getMenuList(storeId)
            }.onFailure {
                sharedPreferencesUtil.addStateLoad("failure")
                setRecipeLoad("failure")
                Log.d(TAG, "uploadRecipe: 실패 - ${it.message}")
            }
        }
    }


    fun getMenuList(storeId: Int) {
        viewModelScope.launch {
            try {
                val recipes = RetrofitUtil.recipeService.getStoreRecipe(storeId)
                _recipeList.postValue(recipes)
                Log.d(TAG, "getMenuList: storeId=$storeId, recipes=${recipes.size}")
            } catch (e: Exception) {
                Log.e(TAG, "getMenuList error: ${e.message}")
                _recipeList.postValue(emptyList())
            }
        }
    }

    fun deleteRecipe(recipeId: Int, storeId: Int){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.recipeService.recipeDelete(recipeId)
            }.onSuccess {
                getMenuList(storeId)
                Log.d(TAG, "deleteRecipe: ${recipeId}")
            }.onFailure {
                Log.d(TAG, "deleteRecipe: ${it}")
            }
        }
    }


    fun searchRecipe(storeId: Int, recipeName: String) {
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