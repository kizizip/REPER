package com.ssafy.reper.data.remote

import com.ssafy.reper.base.ApplicationClass


class RetrofitUtil {
    companion object{
        val noticeService = ApplicationClass.retrofit.create(NotiService::class.java)
        val storeService = ApplicationClass.retrofit.create(StoreService::class.java)
        val recipeService = ApplicationClass.retrofit.create(RecipeService::class.java)
        val storeEmployeeService = ApplicationClass.retrofit.create(StoreEmployeeService::class.java)
        val fcmService = ApplicationClass.retrofit.create(FcmService::class.java)
        val orderService = ApplicationClass.retrofit.create(OrderService::class.java)
        val authService = ApplicationClass.retrofit.create(AuthApi::class.java)
        val kakaoService = ApplicationClass.retrofit.create(KakaoApi::class.java)
        val googleService = ApplicationClass.retrofit.create(GoogleApi::class.java)
        val userService = ApplicationClass.retrofit.create(UserService::class.java)
    }
}