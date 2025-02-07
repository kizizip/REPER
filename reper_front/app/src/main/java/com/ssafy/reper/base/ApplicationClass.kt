package com.ssafy.reper

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "ApplicationClass_싸피"
class ApplicationClass : Application() {
    companion object {
        // 실제 서버 IP 주소를 사용해야 합니다
        // 예: 컴퓨터의 IP 주소 (ipconfig나 ifconfig로 확인)
        // const val SERVER_URL = "http://10.0.2.2:8080/"  // 안드로이드 에뮬레이터용
        const val SERVER_URL = "http://i12d109.p.ssafy.io:48620/api/"
        lateinit var retrofit: Retrofit
    }

    override fun onCreate() {
        super.onCreate()

        // Kakao SDK 초기화
        KakaoSdk.init(this, "4c4f7b722cd48d5d961a6048769dc5e6")

        // Retrofit 초기화
        retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}