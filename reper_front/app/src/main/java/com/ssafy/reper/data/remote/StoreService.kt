package com.ssafy.reper.data.remote

import com.ssafy.reper.data.dto.Employee
import com.ssafy.reper.data.dto.OwnerStore
import com.ssafy.reper.data.dto.RequestStore
import com.ssafy.reper.data.dto.SearchedStore
import com.ssafy.reper.data.dto.Store
import com.ssafy.reper.data.dto.StoreResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StoreService {

    //사장님의 모든 가게 정보를 불러옴
    @GET("stores/owner/{userId}")
    suspend fun findBossStore(@Path("userId") userId :Int): List<SearchedStore>

    //새로운 가게를 추가함
    @POST("stores")
    suspend fun addStore(@Body store : RequestStore)

    //기존의 가게를 삭제함
    @DELETE("stores/{storeId}")
    suspend fun deleteStore(@Path("storeId") storeId: Int)

    @GET("stores/{storeId}/employees")
    suspend fun allEmployee(@Path("storeId") storeId: Int): List<Employee>

    @GET("stores/{storeId}")
    suspend fun getStore(@Path("storeId") storeId: Int) : StoreResponse

    /////////////// 3.8선

    // 특정 알바생의 근무 매장 목록 조회
    @GET("stores/employees/{userId}")
    suspend fun getStoreListByEmployeeId(@Path("userId") userId: Int?): List<Store>


    // 권한 요청 거절 or 알바생 자르기
    @DELETE("stores/{storeId}/employees/{userId}")
    suspend fun deleteEmployee(@Path("storeId") storeId: String, @Path("userId") userId: String)

    // 모든 매장 검색 - 엘라스틱 search
    @GET("stores/search")
    suspend fun searchAllStores(@Query("storeName") storeName: String): List<SearchedStore>

    // 알바생 -> 사장 권한 요청
    @POST("stores/{storeId}/employees/{userId}/approve")
    suspend fun approveEmployee(@Path("storeId") storeId: Int, @Path("userId") userId: Int)

    // OWNER인 {userId}에 해당하는 모든 store를 조회
    @GET("stores/owner/{userId}")
    suspend fun getStoreListByOwnerId(@Path("userId") userId: String): List<OwnerStore>
}



