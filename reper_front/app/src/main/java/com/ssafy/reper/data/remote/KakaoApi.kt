package com.ssafy.reper.data.remote

import com.ssafy.reper.data.dto.KakaoLoginRequest
import com.ssafy.reper.data.dto.KakaoUserInfo
import com.ssafy.reper.data.dto.KakaoLoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface KakaoApi {

    // accessToken을 날리면 사용자의 email, nickname을 불러옵니다.
    @GET("/login/auth/kakao")
    suspend fun getKaKaoUserInfo(@Query("accessToken") accessToken: String): KakaoUserInfo

    // accessToken을 날리면 User 테이블상의 모든 정보를 불러옵니다.
    @POST("/login/auth/kakao")
    suspend fun kakaoLogin(@Body kakaoLoginRequest: KakaoLoginRequest): KakaoLoginResponse

}