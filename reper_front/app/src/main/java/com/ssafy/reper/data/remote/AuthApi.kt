package com.ssafy.reper.data.remote

import com.ssafy.reper.data.dto.LoginResponse
import com.ssafy.reper.data.dto.JoinRequest
import com.ssafy.reper.data.dto.LoginRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 로그인
 * 회원가입
 * api 모아둔곳
 */
interface AuthApi {

    // 사용자 로그인
    @POST("users/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    // 회원가입
    @POST("users")
    suspend fun join(@Body joinRequest: JoinRequest): Int

    // 이메일 중복 체크
    @GET("users/email/check-duplication")
    suspend fun checkEmail(@Query("email") email: String): Boolean


}