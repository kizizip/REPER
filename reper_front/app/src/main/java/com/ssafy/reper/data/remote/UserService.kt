package com.ssafy.reper.data.remote

import com.ssafy.reper.data.dto.User
import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {

    @GET("users/{userId}/info")
    suspend fun getUserInfo(@Path("userId") userId: Int): User
}