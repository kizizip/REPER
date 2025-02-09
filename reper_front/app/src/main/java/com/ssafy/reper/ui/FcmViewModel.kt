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
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.fcmService.getUserToken(userToken)
            }.onSuccess {
                Log.d(TAG, "saveToken: ${userToken}")
            }.onFailure {
                Log.d(TAG, "saveToken: ${it.message}")
            }
        }
    }


    fun sendToUserFCM(userId:Int, tile: String, content:String){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.fcmService.sendToUser(userId,tile,content)
            }.onSuccess {
                Log.d(TAG, "saveToken: ${tile}")
            }.onFailure {
                Log.d(TAG, "saveToken: ${it.message}")
            }
        }
    }

    fun sendToStoreFCM(storeId:Int, tile: String, content:String){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.fcmService.sendToUser(storeId,tile,content)
            }.onSuccess {
                Log.d(TAG, "saveToken: ${tile}")
            }.onFailure {
                Log.d(TAG, "saveToken: ${it.message}")
            }
        }
    }



}