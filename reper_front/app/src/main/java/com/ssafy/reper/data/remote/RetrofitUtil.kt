package com.ssafy.reper.data.remote

import com.ssafy.reper.ApplicationClass


class RetrofitUtil {
    companion object{
        val authService = ApplicationClass.retrofit.create(AuthApi::class.java)
        val kakaoService = ApplicationClass.retrofit.create(KakaoApi::class.java)
    }
}