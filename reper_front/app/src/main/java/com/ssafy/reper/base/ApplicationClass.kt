package com.ssafy.reper.base

import MainActivityViewModel
import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import okhttp3.OkHttpClient
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ssafy.reper.data.local.SharedPreferencesUtil
import com.ssafy.reper.util.ViewModelSingleton
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val TAG = "ApplicationClass_안주현"
class ApplicationClass : Application() {
    companion object{
        // ipconfig를 통해 ip확인하기
        const val SERVER_URL = "http://i12d109.p.ssafy.io:48620/api/"

        lateinit var sharedPreferencesUtil: SharedPreferencesUtil
        lateinit var retrofit: Retrofit
    }


    override fun onCreate() {
        super.onCreate()

        //shared preference 초기화
        sharedPreferencesUtil = SharedPreferencesUtil(applicationContext)

        // OkHttpClient 설정 (타임아웃 관련 설정 추가)
        val client = OkHttpClient.Builder()
            .connectTimeout(600, TimeUnit.SECONDS)  // 연결 타임아웃
            .writeTimeout(600, TimeUnit.SECONDS)    // 쓰기 타임아웃
            .readTimeout(600, TimeUnit.SECONDS)     // 읽기 타임아웃
            .build()

        // Retrofit 초기화
        retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .client(client)  // OkHttpClient 적용
            .addConverterFactory(GsonConverterFactory.create())
/*
        // 레트로핏 인스턴스를 생성하고, 레트로핏에 각종 설정값들을 지정해줍니다.
        // 연결 타임아웃시간은 5초로 지정이 되어있고, HttpLoggingInterceptor를 붙여서 어떤 요청이 나가고 들어오는지를 보여줍니다.
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            // 로그캣에 okhttp.OkHttpClient로 검색하면 http 통신 내용을 보여줍니다.
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build()
//            .addInterceptor(AddCookiesInterceptor())
//            .addInterceptor(ReceivedCookiesInterceptor()).build()

        // 앱이 처음 생성되는 순간, retrofit 인스턴스를 생성
        retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            */
            .build()

        val viewModelStore = ViewModelStore()
        ViewModelSingleton.mainActivityViewModel = ViewModelProvider(viewModelStore,
            ViewModelProvider.AndroidViewModelFactory.getInstance(this))
            .get(MainActivityViewModel::class.java)
    }

    //GSon은 엄격한 json type을 요구하는데, 느슨하게 하기 위한 설정. success, fail이 json이 아니라 단순 문자열로 리턴될 경우 처리..
    val gson : Gson = GsonBuilder()
        .setLenient()
        .create()
}
