package com.ssafy.reper.data.remote

import com.ssafy.smartstore_jetpack.base.ApplicationClass


class RetrofitUtil {
    companion object{
        val noticeService = ApplicationClass.retrofit.create(NotiService::class.java)

    }
}