package com.ssafy.reper.ui.boss

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.reper.data.dto.Notice
import com.ssafy.reper.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

private const val TAG = "NoticeViewModel"

class NoticeViewModel : ViewModel() {

    //라이브 데이터로 관리될 공지 리스트
    private val _noticeList = MutableLiveData<List<Notice>>()
    val noticeList: LiveData<List<Notice>> get() = _noticeList

    fun setNoticeList(notiList : List<Notice>) {
        _noticeList.value = notiList
    }


    //단건공지
    private val _clickNotice = MutableLiveData<Notice?>()
    val clickNotice: LiveData<Notice?> get() = _clickNotice

    // 클릭된 공지 데이터를 설정하는 함수
    fun setClickNotice(noti: Notice?) {
        _clickNotice.value = noti
    }

    fun init(storeId: Int, userId: Int){
        getAllNotice(storeId, userId)
    }


    fun getAllNotice(storeId: Int, userId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitUtil.noticeService.getAllNotice(storeId, userId)
                _noticeList.postValue(response.notices)
            } catch (e: Exception) {
                Log.d(TAG, "getNotice: 공지 리스트 업로드 실패")
            }
        }
    }

    //단건은 받아온 리스트의 정보를 넣는걸로!
    fun getNotice(storeId: Int, noticeId:Int, userId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitUtil.noticeService.getNotice(storeId,noticeId, userId)
                _clickNotice.postValue(response)
            } catch (e: Exception) {
                Log.d(TAG, "getNotice: 공지 조회 실패")
            }
        }
    }


    fun createNotice(storeId: Int, userId: Int, title: String, content: String) {
        viewModelScope.launch {
            try {
               RetrofitUtil.noticeService.createNotice(storeId, userId, title, content)
            } catch (e: Exception) {
                Log.d(TAG, "getNotice: 공지 생성 실패")
            }
        }
    }


    fun modifyNotice(storeId: Int, userId: Int, noticeId:Int, title: String, content: String) {
        viewModelScope.launch {
            try {
                RetrofitUtil.noticeService.modifyNotice(storeId, noticeId, userId, title, content)
            } catch (e: Exception) {
                Log.d(TAG, "getNotice: 공지 수정 실패")
            }
        }
    }



    fun deleteNotice(storeId: Int, userId: Int, noticeId:Int) {
        viewModelScope.launch {
            try {
                RetrofitUtil.noticeService.deleteNotice(storeId, noticeId, userId)
            } catch (e: Exception) {
                Log.d(TAG, "getNotice: 공지 수정 실패")
            }
        }
    }


}