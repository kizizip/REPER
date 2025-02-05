package com.ssafy.reper.data.remote

import ApplicationClass



class RetrofitUtil {
    companion object{
        val noticeService = ApplicationClass.retrofit.create(NotiService::class.java)

    }
}