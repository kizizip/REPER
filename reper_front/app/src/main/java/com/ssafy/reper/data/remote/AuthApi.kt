package com.ssafy.reper.data.remote

import com.ssafy.reper.data.dto.LoginResponse
import com.ssafy.reper.data.dto.JoinRequest
import com.ssafy.reper.data.dto.LoginRequest
import com.ssafy.reper.data.dto.ResponseUserInfo
import com.ssafy.reper.data.dto.UpdateUserRequest
import com.ssafy.reper.data.dto.UserInfo
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 로그인
 * 회원가입
 * api 모아둔곳
 */
interface AuthApi {

    // 회원가입
    @POST("users")
    suspend fun join(@Body joinRequest: JoinRequest): Int

    // 사용자 로그인
    @POST("users/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    // 회원 탈퇴
    @DELETE("users/{userId}")
    suspend fun deleteUser(@Path("userId") userId: Long)


    // 회원정보 수정
    @PATCH("users/{userId}")
    suspend fun updateUser(@Path("userId") userId: Long, @Body updateUserRequest: UpdateUserRequest)

    // 회원정보 반환
    @GET("users/{userId}/info")
    suspend fun getUserInfo(@Path("userId") userId: Long): ResponseUserInfo


    // 이메일 중복 체크
    @GET("users/email/check-duplication")
    suspend fun checkEmail(@Query("email") email: String): Boolean


}