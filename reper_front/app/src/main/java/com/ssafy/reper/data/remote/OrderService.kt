package com.ssafy.reper.data.remote

import com.ssafy.reper.data.dto.Order
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface OrderService {
    // 특정 매장의 전체 주문 조회
    @GET("stores/{storeId}/orders")
    suspend fun getAllOrder(@Path("storeId") storeId: Int):MutableList<Order>

    // 특정 매장의 주문 단건 조회
    @GET("stores/{storeId}/orders/{orderId}")
    suspend fun getOrder(@Path("storeId") storeId: Int, @Path("orderId") orderId: Int):Order

    // 특정 매장의 단건 주문 완료
    @PATCH("stores/{storeId}/orders/{orderId}")
    suspend fun orderComplete(@Path("orderId") orderId: Int) : Boolean
}