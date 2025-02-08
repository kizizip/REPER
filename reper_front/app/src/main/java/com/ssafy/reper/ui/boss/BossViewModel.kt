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

    //레세피 정보 리스트
    private val _recipeList = MutableLiveData<List<Recipe>>()
    val recipeList: MutableLiveData<List<Recipe>> get() = _recipeList

    fun setRecipeList(list: List<Recipe>) {
        _recipeList.value = list
    }

    //승인된 직원 리스트
    private val _accessList = MutableLiveData<List<Employee>>()
    val accessList: MutableLiveData<List<Employee>> get() = _accessList

    //스토어 정보 리스트
    private val _waitingList = MutableLiveData<List<Employee>>()
    val waitingList: MutableLiveData<List<Employee>> get() = _waitingList


    fun getAllEmployee(storeId: Int) {

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
            }.onFailure {
                Log.d(TAG, "deleteStore: ${it.message}")
            }
        }

    }

    fun uploadRecipe(storeId: Int, recipefile: MultipartBody.Part) {
        var message = ""
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.recipeService.recipeUpload(storeId, recipefile)
            }.onSuccess {
                message =  "레시피 업로드 성공"
                Log.d(TAG, "uploadRecipe: 성공")
            }.onFailure {
                Log.d(TAG, "uploadRecipe: ${it.message}")
                message =  "레시피 업로드 실패"
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

    fun deleteRecipe(recipeId: Int):String {
        var message = ""
        viewModelScope.launch {
         runCatching {
             RetrofitUtil.recipeService.recipeDelete(recipeId)
         }.onSuccess {
           message =  "레시피 삭제 성공"
         }.onFailure {
             message =  "레시피 삭제실패"
             Log.d(TAG, "deleteRecipe: 레시피 삭제실패")
         }
        }
        return message
    }

}