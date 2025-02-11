// 패키지 선언
package com.ssafy.reper

// 필요한 라이브러리들을 import
import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.ssafy.reper.base.ReceivedCookiesInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
    }

    // Application 클래스의 onCreate 메서드 오버라이드
    override fun onCreate() {
        // 부모 클래스의 onCreate 호출
        super.onCreate()

        // Kakao SDK 초기화 (카카오 로그인 기능을 위한 설정)
        KakaoSdk.init(this, "4c4f7b722cd48d5d961a6048769dc5e6")

        // OkHttp 클라이언트 설정
        val client = OkHttpClient.Builder()
            .addInterceptor(ReceivedCookiesInterceptor())  // 쿠키 인터셉터 추가
            .build()

        // Retrofit 초기화 및 설정
        retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)           // 기본 URL 설정
            .client(client)                // OkHttp 클라이언트 설정
            .addConverterFactory(GsonConverterFactory.create())  // JSON 변환기 추가
            .build()
    }
}