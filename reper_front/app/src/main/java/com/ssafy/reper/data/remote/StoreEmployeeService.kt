package com.ssafy.reper.data.remote

import com.ssafy.reper.data.dto.Employee
import com.ssafy.reper.data.dto.StoreResponse
import com.ssafy.reper.data.dto.StoreResponseUser
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface StoreEmployeeService {

    // 가게의 모든 직원을 조회함
    @GET("stores/{storeId}/employees")
    suspend fun allEmployee(@Path("storeId") storeId: Int): List<Employee>

    //알바생의 권한요청
    @POST("stores/{storeId}/employees/{userId}/approve")
    suspend fun requireAccessEmployee(@Path("storeId") storeId : Int, @Path("userId") userUd : Int) : Boolean

    //사장님의 권한 승인
    @PATCH("stores/{storeId}/employees/{userId}/approve")
    suspend fun acceptEmployee(@Path("storeId") storeId : Int, @Path("userId") userUd : Int)

    // 권한삭제(직원 해고) & 권한 거절
    @DELETE("stores/{storeId}/employees/{userId}")
    suspend fun deleteEmployee(@Path("storeId") storeId : Int, @Path("userId") userUd : Int)


    @GET("stores/employees/{userId}")
    suspend fun getUserStore(@Path("userId") userUd: Int) : List<StoreResponseUser>

}