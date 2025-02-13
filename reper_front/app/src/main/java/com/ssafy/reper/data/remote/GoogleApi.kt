package com.ssafy.reper.data.remote

import com.ssafy.reper.data.dto.GoogleLoginResponse
import retrofit2.http.POST
import retrofit2.http.Path

interface GoogleApi {

    // 구글 로그인 성공시 사용자 정보 반환
    @POST("users/cookie/{email}")
    suspend fun googleLogin(@Path("email") email: String): GoogleLoginResponse
}