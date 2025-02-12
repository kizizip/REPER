package com.ssafy.reper.data.remote

import com.ssafy.reper.data.dto.Notice
import com.ssafy.reper.data.dto.NoticeRequest
import com.ssafy.reper.data.dto.StoreNoticeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NotiService {

    //가게에 해당하는 모든 공지를 가져옵니다.
    @GET("stores/{storeId}/notices")
    suspend fun getAllNotice(@Path("storeId") storeId: Int, @Query("userId") userId: Int) :List<StoreNoticeResponse>

    // notiId에 해당하는 공지를 가져옵니다.
    @GET("stores/{storeId}/notices/{noticeId}")
    suspend fun getNotice(@Path("storeId") storeId: Int, @Path("noticeId") noticeId:Int, @Query("userId") userId: Int): Notice

    //가게에 공지를 등록한다.
    @POST("stores/{storeId}/notices")
    suspend fun createNotice(@Path("storeId") storeId: Int, @Body request: NoticeRequest): Notice

    //공지를 수정한다.
    @PUT("stores/{storeId}/notices/{noticeId}")
    suspend fun modifyNotice(@Path("storeId") storeId: Int, @Path("noticeId") noticeId:Int, @Body request: NoticeRequest)


    //공지를 수정한다.
    @DELETE("stores/{storeId}/notices/{noticeId}")
    suspend fun deleteNotice(@Path("storeId") storeId: Int, @Path("noticeId") noticeId:Int, @Body request: NoticeRequest)

    //공지를 삭제한다.
    @HTTP(method = "DELETE", path = "stores/{storeId}/notices/{noticeId}", hasBody = true)
    suspend fun deleteNotice(
        @Path("storeId") storeId: Int,
        @Path("noticeId") noticeId: Int,
        @Body requestBody: Map<String, Int>
    ): Response<Void?>

    //제목으로 공지를 검색합니다
    @GET("stores/{storeId}/notices/search/title")
    suspend fun searchNoticeTitle(@Path("storeId") storeId : Int, @Query ("titleKeyword")titleKeyword: String): List<Notice>

    //내용으로 공지를 검색합니다
    @GET("stores/{storeId}/notices/search/content")
    suspend fun searchNoticeContent(@Path("storeId") storeId : Int, @Query ("contentKeyword")contentKeyword: String): List<Notice>

}