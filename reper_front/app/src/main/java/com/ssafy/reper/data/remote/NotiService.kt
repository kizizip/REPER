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

    //가게에 해당하는 모든 공지를 가져옵니다.
    @GET("stores/{storeId}/notices")
    suspend fun getAllNotice(@Path("storeId") storeId: Int, @Body userId: Int) : StoreNoticeResponse

    // notiId에 해당하는 공지를 가져옵니다.
    @GET("stores/{storeId}/notices/{noticeId}")
    suspend fun getNotice(@Path("storeId") storeId: Int, @Path("noticeId") noticeId:Int, @Body userId : Int): Notice

    //가게에 공지를 등록한다.
    @POST("stores/{storeId}/notices")
    suspend fun createNotice(@Path("storeId") storeId: Int, @Body userId: Int, @Body title: String, @Body content: String)

    //공지를 수정한다.
    @PUT("stores/{storeId}/notices/{noticeId}")
    suspend fun modifyNotice(@Path("storeId") storeId: Int, @Path("noticeId") noticeId:Int, @Body userId : Int, @Body title: String, @Body content: String)

    //공지를 삭제한다.
    @DELETE("stores/{storeId}/notices/{noticeId}")
    suspend fun deleteNotice(@Path("storeId") storeId: Int, @Path("noticeId") noticeId:Int, @Body userId : Int)

}