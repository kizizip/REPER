package com.ssafy.reper.data.remote

import com.ssafy.reper.data.dto.Store
import com.ssafy.reper.data.dto.RequestStore
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BossService {

    //사장님의 모든 가게 정보를 불러옴
    @GET("stores/owner/{userId}")
    suspend fun findBossStore(@Path("userId") userId :Int): List<Store>

    //새로운 가게를 추가함
    @POST("stores")
    suspend fun addStore(@Body store : RequestStore)

    //기존의 가게를 삭제함
    @DELETE("stores/{storeId}/employees/{userId}")
    suspend fun deleteEmployee(@Path("storeId") storeId: Int)

}

    //가게의 모든 직원을 조회함
//    @GET("stores/{storeId}/employees")
//    suspend fun allEmployee(@Path("storeId") storeId: Int): List<Employee>

   //
//    @DELETE("stores/{storeId}")
//    suspend fun updateStore(@Path("storeId") storeId: Int)
//
//
//    @POST("stores/{storeId}/empployees/{userId}/approve")
//    suspend fun accessEmployee(@Path("storeId") storeId : Int, @Path ("userId") userUd : Int) : Boolean


