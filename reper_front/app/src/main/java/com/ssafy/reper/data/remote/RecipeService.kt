package com.ssafy.reper.data.remote

import com.ssafy.reper.data.dto.Recipe
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.File

interface RecipeService {
    // 레시피 파일 업로드 <-- 완벽한 것 아님. 수정 필요
//    @POST("upload")
//    suspend fun uploadFile(@Query("storeId") storeId: Int, @Body pdf: File):String

    // 특정 매장의 전체 레시피 조회
    @GET("stores/{storeId}/recipes")
    suspend fun getAllRecipes(@Path("storeId") storeId: Int): MutableList<Recipe>

    // 특정 매장의 단일 레시피 조회
    @GET("recipes/{recipeId}")
    suspend fun getRecipe(@Path("recipeId") recipeId: Int): Recipe

    // 특정 매장의 단일 레시피 삭제
    @DELETE("recipes/{recipeId}")
    suspend fun deleteRecipe(@Path("recipeId") recipeId: Int) :String
}