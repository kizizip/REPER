package com.ssafy.reper.data.remote

import com.ssafy.reper.data.dto.UserToken
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FcmService {

    //토큰 저장하기
    @POST("token/save")
    suspend fun getUserToken(@Body request: UserToken)

    //한명에게 알림 보내기
    @POST("fcm/sendToUser/{userId}")
    suspend fun sendToUser(@Path("userId")userId: Int, @Query("title") title:String, @Query("body") content:String)

    //가게직원 모두에게 알림보내기
    @POST("fcm/sendToStore/{storeId}")
    suspend fun sendToStore(@Path("storeId")storeId: Int, @Query("title") title:String, @Query("body") content:String)

}