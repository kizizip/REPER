package com.ssafy.reper.base

import MainActivityViewModel
import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kakao.sdk.common.KakaoSdk
import com.ssafy.reper.data.local.SharedPreferencesUtil
import com.ssafy.reper.util.ViewModelSingleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// 로그 태그 상수 선언
private const val TAG = "ApplicationClass_싸피"

// Application을 상속받는 ApplicationClass 선언 - 앱의 전역 설정을 담당
class ApplicationClass : Application() {
    
    // companion object를 통해 정적 멤버 선언
    companion object {
        // 서버 URL 상수 선언 (실제 서버 주소로 설정됨)
        // const val SERVER_URL = "http://10.0.2.2:8080/"  // 안드로이드 에뮬레이터용


        const val SERVER_URL = "http://i12d109.p.ssafy.io:48620/api/"
        
        // Retrofit 인스턴스를 lateinit으로 선언 (나중에 초기화)
        lateinit var retrofit: Retrofit

        //shared preference 초기화
        lateinit var sharedPreferencesUtil: SharedPreferencesUtil
    }

    // Application 클래스의 onCreate 메서드 오버라이드
    override fun onCreate() {
        // 부모 클래스의 onCreate 호출
        super.onCreate()

        sharedPreferencesUtil = SharedPreferencesUtil(applicationContext)

        // OkHttpClient 설정 (타임아웃 관련 설정 추가)
        val client = OkHttpClient.Builder()
            .connectTimeout(600, TimeUnit.SECONDS)  // 연결 타임아웃
            .writeTimeout(600, TimeUnit.SECONDS)    // 쓰기 타임아웃
            .readTimeout(600, TimeUnit.SECONDS)     // 읽기 타임아웃
            .addInterceptor(ReceivedCookiesInterceptor(applicationContext))  // 쿠키 인터셉터 추가
            .build()

        // Kakao SDK 초기화 (카카오 로그인 기능을 위한 설정)
        KakaoSdk.init(this, "4c4f7b722cd48d5d961a6048769dc5e6")

/*
        // OkHttp 클라이언트 설정
        val client = OkHttpClient.Builder()
            .addInterceptor(ReceivedCookiesInterceptor())  // 쿠키 인터셉터 추가
            .build()
*/
        // Retrofit 초기화 및 설정
        retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .client(client)  // OkHttpClient 적용
            .addConverterFactory(GsonConverterFactory.create())
            .build()
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
