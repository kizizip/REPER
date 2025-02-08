package com.ssafy.reper.data.remote

import com.ssafy.reper.ApplicationClass


class RetrofitUtil {
    companion object{
        val noticeService = ApplicationClass.retrofit.create(NotiService::class.java)
        val storeService = ApplicationClass.retrofit.create(StoreService::class.java)
        val storeEmployeeService = ApplicationClass.retrofit.create(StoreEmployeeService::class.java)

        val authService = ApplicationClass.retrofit.create(AuthApi::class.java)
        val kakaoService = ApplicationClass.retrofit.create(KakaoApi::class.java)
    }
}