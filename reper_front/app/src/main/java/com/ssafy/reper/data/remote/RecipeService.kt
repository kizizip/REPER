package com.ssafy.reper.data.remote

import com.ssafy.reper.data.dto.Recipe
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeService {

    //pdf파일을 업로드하고 db에 레시피를 저장합니다.
    @POST("upload")
    suspend fun recipeUpload(@Query("storeId")storeId:Int, @Body recipeFile: MultipartBody.Part )

    //가게의 레시피를 삭제합니다.
    @DELETE("recipes/{recipe}")
    suspend fun recipeDelete(@Path("recipeId") recipeId: Int)

    //가게에 해당하는 레시피를 불러옵니다
    @GET("stores/{storeId}/recipe")
    suspend fun getStoreRecipe(@Path("storeId") storeId: Int):List<Recipe>


}