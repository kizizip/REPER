package com.ssafy.reper.data.remote

import com.ssafy.reper.base.ApplicationClass
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class RetrofitUtil {
    companion object{

        val noticeService = ApplicationClass.retrofit.create(NotiService::class.java)
        val storeService = ApplicationClass.retrofit.create(StoreService::class.java)
        val recipeService = ApplicationClass.retrofit.create(RecipeService::class.java)
        val storeEmployeeService = ApplicationClass.retrofit.create(StoreEmployeeService::class.java)
//        val fcmService = ApplicationClass.retrofit.create(FcmService::class.java)

//        val authService = ApplicationClass.retrofit.create(AuthApi::class.java)
//        val kakaoService = ApplicationClass.retrofit.create(KakaoApi::class.java)
    }
}