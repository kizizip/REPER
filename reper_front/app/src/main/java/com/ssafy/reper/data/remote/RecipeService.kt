package com.ssafy.reper.data.remote


import com.ssafy.reper.data.dto.Recipe
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import com.ssafy.reper.data.dto.FavoriteRecipe
import com.ssafy.reper.data.dto.RecipeResponse
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeService {

    //pdf파일을 업로드하고 db에 레시피를 저장합니다.
    @Multipart
    @POST("upload")
    suspend fun recipeUpload(
        @Query("storeId") storeId: Int,
        @Part recipeFile: MultipartBody.Part
    ):String


    //가게의 레시피를 삭제합니다.
    @DELETE("recipes/{recipeId}")
    suspend fun recipeDelete(@Path("recipeId") recipeId: Int)

    //가게에 해당하는 레시피를 불러옵니다
    @GET("stores/{storeId}/recipes")
    suspend fun getStoreRecipe(@Path("storeId") storeId: Int):List<Recipe>

    //레시피 검색
    @GET("stores/{storeId}/recipes/search")
    suspend fun searchRecipe(@Path("storeId") storeId: Int, @Query("recipeName")recipeName: String):List<Recipe>

    // 특정 매장의 전체 레시피 조회
    @GET("stores/{storeId}/recipes")
    suspend fun getAllRecipes(@Path("storeId") storeId: Int): MutableList<Recipe>

    // 특정 매장의 단일 레시피 조회
    @GET("recipes/{recipeId}")
    suspend fun getRecipe(@Path("recipeId") recipeId: Int): Recipe

    // 특정 매장의 레시피 이름 검색
    @GET("stores/{storeId}/recipes/search")
    suspend fun searchRecipeName(@Path("storeId") storeId: Int, @Query("recipeName") recipeName:String) : MutableList<RecipeResponse>

    // 특정 매장의 레시피 재료 포함 검색
    @GET("stores/{storeId}/recipes/search/include")
    suspend fun searchRecipeInclude(@Path("storeId") storeId: Int, @Query("ingredient") ingredient:String):MutableList<RecipeResponse>

    // 특정 매장의 레시피 재료 제외 검색
    @GET("stores/{storeId}/recipes/search/exclude")
    suspend fun searchRecipeExclude(@Path("storeId") storeId: Int, @Query("ingredient") ingredient:String) :MutableList<RecipeResponse>

    // 즐겨찾기 레시피 등록
    @POST("user/{userId}/favorites/recipe/{recipeId}")
    suspend fun likeRecipe(@Path("userId") userId: Int, @Path("recipeId") recipeId:Int)

    // 즐겨찾기 레시피 삭제
    @DELETE("user/{userId}/favorites/recipe/{recipeId}")
    suspend fun unLikeRecipe(@Path("userId") userId: Int, @Path("recipeId") recipeId:Int)

    // 즐겨찾기 레시피 전체 조회
    @GET("store/{storeId}/user/{userId}/favorites")
    suspend fun getLikeRecipes(@Path("storeId") storeId: Int, @Path("userId") userId:Int) : MutableList<FavoriteRecipe>
}