package com.ssafy.reper.base

import android.content.Context
import android.util.Log
import com.ssafy.reper.data.local.SharedPreferencesUtil
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

private const val TAG = "ReceivedCooki_싸피"

class ReceivedCookiesInterceptor(private val context: Context) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse: Response = chain.proceed(chain.request())
        val sharedPreferencesUtil = SharedPreferencesUtil(context)

        if (originalResponse.headers("Set-Cookie").isNotEmpty()) {
            val cookies = HashSet<String>()
            
            for (header in originalResponse.headers("Set-Cookie")) {
                cookies.add(header)
                // 각 쿠키의 상세 내용 로깅
                Log.d(TAG, "쿠키 전체 문자열: $header")
                
                // 쿠키 구성요소 분석
                header.split(";").forEach { part ->
                    Log.d(TAG, "쿠키 구성요소: $part")
                    
                    // 쿠키의 이름과 값 분리
                    if (part.contains("=")) {
                        val keyValue = part.trim().split("=")
                        if (keyValue.size == 2) {
                            Log.d(TAG, "쿠키 키: ${keyValue[0]}, 값: ${keyValue[1]}")
                        }
                    }
                }
                Log.d(TAG, "------------------------")

                // Max-Age 값 추출
                header.split(";").forEach { part ->
                    if (part.trim().startsWith("Max-Age=")) {
                        val maxAge = part.trim().split("=")[1].toLongOrNull()
                        if (maxAge != null) {
                            // 현재 시간 + Max-Age(초)를 밀리초로 변환하여 저장
                            val expiryTime = System.currentTimeMillis() + (maxAge * 1000)
                            sharedPreferencesUtil.saveCookieExpiry(expiryTime)
                            Log.d(TAG, "쿠키 만료 시간 저장: ${expiryTime}")
                        }
                    }
                }
            }

            Log.d(TAG, "전체 쿠키 세트: $cookies")
        } else {
            Log.d(TAG, "Set-Cookie 헤더가 없습니다")
        }
        
        return originalResponse
    }
}
