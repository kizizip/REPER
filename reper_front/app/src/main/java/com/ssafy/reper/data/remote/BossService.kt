package com.ssafy.reper.data.remote

import com.ssafy.reper.data.dto.BossStore
import com.ssafy.reper.data.dto.Employee
import com.ssafy.reper.data.dto.RequestStore
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BossService {

    @GET("stores/owner/{userId}")
    suspend fun findBossStore(@Path("userId") userId :Int): List<BossStore>

    @GET("stores/{storeId}/employees")
    suspend fun allEmployee(@Path("storeId") storeId: Int): List<Employee>

//    @DELETE("stores/{storeId}")
//    suspend fun updateStore(@Path("storeId") storeId: Int)
//
//    @POST("stores")
//    suspend fun addStore(@Body store : RequestStore)

    @POST("stores/{storeId}/empployees/{userId}/approve")
    suspend fun accessEmployee(@Path("storeId") storeId : Int, @Path ("userId") userUd : Int) : Boolean

    @DELETE("stores/{storeId}/employees/{userId}")
    suspend fun deleteEmployee(@Path("storeId") storeId: Int, @Path("userId") userId: Int)

}