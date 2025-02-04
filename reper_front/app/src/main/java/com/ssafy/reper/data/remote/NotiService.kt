package com.ssafy.reper.data.remote

import com.ssafy.reper.data.dto.Notice
import com.ssafy.reper.data.dto.StoreNoticeResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotiService {


    // notiId에 해당하는 공지를 가져옵니다.
    @GET("stores/{storeId}/notices/{noticeId}")
    suspend fun getNotice(@Path("storeId") storeId: String, @Path("noticeId") noticeId:String): StoreNoticeResponse

    //

//    // 특정 도시락 아이디에 해당하는 쇼핑 카트 아이템 조회
//    @GET("rest/shoppingCart/dosirack/{dosirackId}")
//    suspend fun getCartItemsByDosirock(@Path("dosirackId") dosirackId: Int): List<CartOnly>
//
//    // 쇼핑 카트에 아이템 추가
//    @POST("rest/shoppingCart")
//    suspend fun addCartItem(@Body shoppingCart: CartOnly)
//
//    // 쇼핑 카트 아이템 수정
//    @PUT("rest/shoppingCart/{cartId}")
//    suspend fun updateCartItem(@Path("cartId") cartId: Int, @Body shoppingCart: CartOnly)
//
//    // 쇼핑 카트 아이템 삭제
//    @DELETE("rest/shoppingCart/{cartId}")
//    suspend fun removeCartItem(@Path("cartId") cartId: Int)
//
//    // 모든 쇼핑 카트 아이템 조회
//    @GET("rest/shoppingCart")
//    suspend fun getAllCartItems(): List<CartOnly>

}