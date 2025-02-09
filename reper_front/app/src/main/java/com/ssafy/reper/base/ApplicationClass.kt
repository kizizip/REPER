package com.ssafy.reper

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val TAG = "ApplicationClass_싸피"
class ApplicationClass : Application() {
    companion object {
        // 실제 서버 IP 주소를 사용해야 합니다
        // 예: 컴퓨터의 IP 주소 (ipconfig나 ifconfig로 확인)
        // const val SERVER_URL = "http://10.0.2.2:8080/"  // 안드로이드 에뮬레이터용
        const val SERVER_URL = "http://192.168.1.181:8080/api/"

        // Retrofit 객체를 여기서 선언
        lateinit var retrofit: Retrofit
    }

    override fun onCreate() {
        super.onCreate()

        // Kakao SDK 초기화
        KakaoSdk.init(this, "4c4f7b722cd48d5d961a6048769dc5e6")

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
            .build()
    }
}
