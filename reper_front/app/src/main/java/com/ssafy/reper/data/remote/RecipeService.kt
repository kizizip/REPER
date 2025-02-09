package com.ssafy.reper.data.remote

import com.ssafy.reper.data.dto.Recipe
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeService {
    // 특정 매장의 전체 레시피 조회
    @GET("stores/{storeId}/recipes")
    suspend fun getAllRecipes(@Path("storeId") storeId: Int): MutableList<Recipe>

    // 특정 매장의 단일 레시피 조회
    @GET("recipes/{recipeId}")
    suspend fun getRecipe(@Path("recipeId") recipeId: Int): Recipe

    // 특정 매장의 레시피 이름 검색
    @GET("stores/{storeId}/recipes/search")
    suspend fun searchRecipeName(@Path("storeId") storeId: Int, @Query("recipeName") recipeName:String) :MutableList<Recipe>

    // 특정 매장의 레시피 재료 포함 검색
    @GET("stores/{storeId}/recipes/search/include")
    suspend fun searchRecipeInclude(@Path("storeId") storeId: Int, @Query("ingredient") ingredient:String) :MutableList<Recipe>

    // 특정 매장의 레시피 재료 제외 검색
    @GET("stores/{storeId}/recipes/search")
    suspend fun searchRecipeExclude(@Path("storeId") storeId: Int, @Query("ingredient") ingredient:String) :MutableList<Recipe>
}