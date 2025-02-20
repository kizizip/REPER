package com.ssafy.reper.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.reper.data.dto.UserToken
import com.ssafy.reper.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

private const val TAG = "FcmViewModel_싸피"
class FcmViewModel:ViewModel() {

    fun saveToken(userToken: UserToken){
        viewModelScope.launch{
            runCatching {
                RetrofitUtil.fcmService.getUserToken(userToken)
            }.onSuccess {
                Log.d(TAG, "saveToken: ${userToken}")
            }.onFailure {
                Log.d(TAG, "saveToken: ${it.message}")
            }
        }
    }


    fun sendToUserFCM(userId: Int, title: String, content: String, targetFragment: String, requestId: Int):String{
       var result = "실패"
        viewModelScope.launch{
            runCatching {
                RetrofitUtil.fcmService.sendToUser(userId, title, content, targetFragment, requestId)
                Log.d(TAG, "sendToUserFCM: 함수실행중")
            }.onSuccess {
                Log.d(TAG, "sendToUserFCM: ${title}")
               result = "성공"
            }.onFailure {
                Log.d(TAG, "sendToUserFCM: ${it.message}")
            }
        }
        return result
    }

    fun sendToStoreFCM(storeId: Int, title: String, content: String, targetFragment: String, requestId: Int) {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.fcmService.sendToStore(storeId, title, content, targetFragment, requestId)
            }.onSuccess {
                Log.d(TAG, "sendToStoreFCM:성공공다리 ${title}")
            }.onFailure {
                Log.d(TAG, "sendToStoreFCM: ${it.message}")
            }
        }
    }

    fun  deleteUserToken(userId : Int){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.fcmService.deleteSUserToken(userId)
            }.onSuccess {
                Log.d(TAG, "deleteUserToken: ${userId}")
            }.onFailure {
                Log.d(TAG, "deleteUserToken: ${it.message}")
                Log.d(TAG, "deleteUserToken: ${it}")
            }
        }
    }


    fun  deleteStoreToken(storeId: Int){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.fcmService.deleteStoreToken(storeId)
            }.onSuccess {
                Log.d(TAG, "deleteStoreToken: ${storeId}")
            }.onFailure {
                Log.d(TAG, "deleteUserToken: ${it.message}")
            }
        }
    }

}