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


    fun sendToUserFCM(userId:Int, title: String, content:String){
        viewModelScope.launch{
            runCatching {
                RetrofitUtil.fcmService.sendToUser(userId,title,content)
                Log.d(TAG, "sendToUserFCM: 함수실행중")
            }.onSuccess {
                Log.d(TAG, "sendToUserFCM: ${title}")
            }.onFailure {
                Log.d(TAG, "sendToUserFCM: ${it.message}")
            }
        }
    }

    fun sendToStoreFCM(storeId:Int, tile: String, content:String){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.fcmService.sendToStore(storeId,tile,content)
            }.onSuccess {
                Log.d(TAG, "sendToStoreFCM: ${tile}")
            }.onFailure {
                Log.d(TAG, "sendToStoreFCM: ${it.message}")
            }
        }
    }



}