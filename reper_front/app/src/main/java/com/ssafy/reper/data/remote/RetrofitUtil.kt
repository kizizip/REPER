package com.ssafy.reper.data.remote

import com.ssafy.reper.base.ApplicationClass


class RetrofitUtil {
    companion object{
        val orderService = ApplicationClass.retrofit.create(OrderService::class.java)
        val recipeService = ApplicationClass.retrofit.create(RecipeService::class.java)
        val noticeService = ApplicationClass.retrofit.create(NotiService::class.java)
        val bossService = ApplicationClass.retrofit.create(BossService::class.java)
        val authService = ApplicationClass.retrofit.create(AuthApi::class.java)
        val kakaoService = ApplicationClass.retrofit.create(KakaoApi::class.java)
    }
}