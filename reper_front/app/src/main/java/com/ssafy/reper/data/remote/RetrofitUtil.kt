package com.ssafy.reper.data.remote

import com.ssafy.smartstore_jetpack.base.ApplicationClass

class RetrofitUtil {
    companion object{
        val orderService = ApplicationClass.retrofit.create(OrderService::class.java)
        val authService = ApplicationClass.retrofit.create(AuthApi::class.java)
        val kakaoService = ApplicationClass.retrofit.create(KakaoApi::class.java)
    }
}