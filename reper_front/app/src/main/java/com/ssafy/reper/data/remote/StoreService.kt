package com.ssafy.reper.data.remote

import com.ssafy.reper.data.dto.Employee
import com.ssafy.reper.data.dto.Store
import com.ssafy.reper.data.dto.RequestStore
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StoreService {

    //사장님의 모든 가게 정보를 불러옴
    @GET("stores/owner/{userId}")
    suspend fun findBossStore(@Path("userId") userId :Int): List<Store>

    //새로운 가게를 추가함
    @POST("stores")
    suspend fun addStore(@Body store : RequestStore)

    //기존의 가게를 삭제함
    @DELETE("stores/{storeId}")
    suspend fun deleteStore(@Path("storeId") storeId: Int):String

    //가게명으로 매장검색
    @GET("store/search")
    suspend fun searchStore(@Query ("storeName")storeName : String) : List<Store>

    @GET("stores/{storeId}/employees")
    suspend fun allEmployee(@Path("storeId") storeId: Int): List<Employee>


}



