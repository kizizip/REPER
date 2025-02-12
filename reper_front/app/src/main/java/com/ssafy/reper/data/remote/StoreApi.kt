package com.ssafy.reper.data.remote

import com.ssafy.reper.data.dto.OwnerStore
import com.ssafy.reper.data.dto.Store
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StoreApi {
    // 특정 알바생의 근무 매장 목록 조회
    @GET("stores/employees/{userId}")
    suspend fun getStoreListByEmployeeId(@Path("userId") userId: String): List<Store>

    // 권한 요청 거절 or 알바생 자르기
    @DELETE("stores/{storeId}/employees/{userId}")
    suspend fun deleteEmployee(@Path("storeId") storeId: String, @Path("userId") userId: String)
    
    // 모든 매장 검색 - 엘라스틱 search
    @GET("stores/search")
    suspend fun searchAllStores(@Query("storeName") storeName: String): List<Store>

    // 알바생 -> 사장 권한 요청
    @POST("stores/{storeId}/employees/{userId}/approve")
    suspend fun approveEmployee(@Path("storeId") storeId: String, @Path("userId") userId: String)

    // OWNER인 {userId}에 해당하는 모든 store를 조회
    @GET("stores/owner/{userId}")
    suspend fun getStoreListByOwnerId(@Path("userId") userId: String): List<OwnerStore>
}