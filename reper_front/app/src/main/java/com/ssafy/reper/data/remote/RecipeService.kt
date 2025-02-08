package com.ssafy.reper.data.remote

import android.hardware.camera2.CameraExtensionSession.StillCaptureLatency
import com.google.android.gms.common.api.Response
import com.ssafy.reper.data.dto.Recipe
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeService {

    //pdf파일을 업로드하고 db에 레시피를 저장합니다.
    @Multipart
    @POST("upload")
    suspend fun recipeUpload(
        @Query("storeId") storeId: Int,
        @Part recipeFile: MultipartBody.Part
    )


    //가게의 레시피를 삭제합니다.
    @DELETE("recipes/{recipeId}")
    suspend fun recipeDelete(@Path("recipeId") recipeId: Int)

    //가게에 해당하는 레시피를 불러옵니다
    @GET("stores/{storeId}/recipes")
    suspend fun getStoreRecipe(@Path("storeId") storeId: Int):List<Recipe>

    //레시피 검색
    @GET("stores/{storeId}/recipes/search")
    suspend fun searchRecipe(@Path("storeId") storeId: Int, @Query("recipeName")recipeName: String):List<Recipe>

}