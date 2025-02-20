package com.ssafy.reper.data.remote

import com.ssafy.reper.data.dto.UserToken
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FcmService {

    //토큰 저장하기
    @POST("token/save")
    suspend fun getUserToken(@Body request: UserToken)

    //한명에게 알림 보내기
    @POST("fcm/sendToUser/{userId}")
    suspend fun sendToUser(@Path("userId")userId: Int, @Query("title") title:String, @Query("body") content:String,
                           @Query("targetFragment") targetFragment: String,@Query("requestId") requestId: Int)

    //가게직원 모두에게 알림보내기
    @POST("fcm/sendToStore/{storeId}")
    suspend fun sendToStore(@Path("storeId")storeId: Int, @Query("title") title:String, @Query("body") content:String,
                            @Query("targetFragment") targetFragment: String,@Query("requestId") requestId: Int)

   //가게직원 모두의 토큰 삭제
    @DELETE("token/deleteTokensForStore/{storeId}")
    suspend fun deleteStoreToken(@Path("storeId") storeId: Int)

    //한 직원의 토큰 삭제-->로그 아웃, 권한 삭제..., 알림 끄기
    @DELETE("token/deleteTokenForUser/{userId}")
    suspend fun deleteSUserToken(@Path("userId") userId: Int): String

}