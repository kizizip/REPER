package com.ssafy.reper.ui.boss

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.reper.data.dto.Notice
import com.ssafy.reper.data.dto.NoticeRequest
import com.ssafy.reper.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

private const val TAG = "NoticeViewModel"

class NoticeViewModel : ViewModel() {

    //라이브 데이터로 관리될 공지 리스트
    private val _noticeList = MutableLiveData<List<Notice>>()
    val noticeList: LiveData<List<Notice>> get() = _noticeList
    fun setNoticeList(notiList: List<Notice>) {
        _noticeList.value = notiList
    }

    //단건공지
    private val _clickNotice = MutableLiveData<Notice?>()
    val clickNotice: LiveData<Notice?> get() = _clickNotice

    // 클릭된 공지 데이터를 설정하는 함수
    fun setClickNotice(noti: Notice?) {
        _clickNotice.value = noti
        Log.d(TAG, "setClickNotice: ${noti}")
    }


    fun init(storeId: Int, userId: Int){
        getAllNotice(storeId, userId)
    }

    var type = ""


    fun getAllNotice(storeId: Int, userId: Int) {
        viewModelScope.launch {
            try {
                // storeId가 0이면 빈 리스트로 설정
                if (storeId == 0) {
                    _noticeList.postValue(emptyList()) // 빈 리스트로 설정
                } else {
                    val response = RetrofitUtil.noticeService.getAllNotice(storeId, userId)
                    // 정상적인 공지 리스트를 저장
                    _noticeList.postValue(response.getOrNull(0)?.notices ?: emptyList())
                }
                Log.d(TAG, "getAllNotice: ${_noticeList.value}")
            } catch (e: Exception) {
                Log.d(TAG, "getAllNotice: ${e.message}")
                Log.d(TAG, "getAllNotice: 공지 리스트 업로드 실패")
            }
        }
    }

    //단건은 받아온 리스트의 정보를 넣는걸로!
    fun getNotice(storeId: Int, noticeId:Int, userId: Int):String {
        var request = ""
        viewModelScope.launch{
            
            runCatching { 
                RetrofitUtil.noticeService.getNotice(storeId,noticeId, userId) 
            }.onSuccess {
                Log.d(TAG, "getNotice: ${it}")
                setClickNotice(it)
                request ="성공"
            }.onFailure {
                Log.d(TAG, "getNotice: ${it.message}")
                request="실패"
            }
            
        }
        return request
    }

    suspend fun createNotice(storeId: Int, userId: Int, title: String, content: String): Notice? {
        return runCatching {
            RetrofitUtil.noticeService.createNotice(storeId, NoticeRequest(userId, title, content))
        }.onSuccess {
            getAllNotice(storeId, userId)
            Log.d(TAG, "공지 생성 성공: $it")
        }.onFailure {
            Log.d(TAG, "공지 생성 실패: ${it.message}")
        }.getOrNull()  // 성공하면 Notice 반환, 실패하면 null 반환
    }



    fun modifyNotice(storeId: Int, userId: Int, noticeId:Int, title: String, content: String) {
        viewModelScope.launch {
            try {
                RetrofitUtil.noticeService.modifyNotice(storeId, noticeId,  NoticeRequest(userId,title, content))
                getAllNotice(storeId, userId)
            } catch (e: Exception) {
                Log.d(TAG, "getNotice: 공지 수정 실패")
                Log.d(TAG, "modifyNotice: ${e.message}")
                Log.d(TAG, "modifyNotice: ${e}")
            }
        }
    }



    fun deleteNotice(storeId: Int, noticeId:Int, requestBody: Map<String, Int>) {
        viewModelScope.launch {
            try {
                RetrofitUtil.noticeService.deleteNotice(storeId, noticeId, requestBody )
                getAllNotice(storeId, 1)
            } catch (e: Exception) {
                Log.d(TAG, "getNotice: 공지 삭제 실패")
                Log.d(TAG, "deleteNotice: ${e.message}")
            }
        }
    }

    fun searchNoticeTitle(storeId: Int, noticeTitle:String){
        viewModelScope.launch{
            runCatching {
                RetrofitUtil.noticeService.searchNoticeTitle(storeId, noticeTitle)
            }.onSuccess {
                setNoticeList(it)
            }.onFailure {

            }
        }

    }

    fun searchNoticeContent(storeId: Int, contentTitle:String){
        viewModelScope.launch{
            runCatching {
                RetrofitUtil.noticeService.searchNoticeContent(storeId, contentTitle)
            }.onSuccess {
                setNoticeList(it)
            }.onFailure {

            }
        }
    }

}