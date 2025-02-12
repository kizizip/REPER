package com.ssafy.reper.data.dto

data class RecipeResponse(
    val category: String,
    val ingredients: List<String>,
    val likedRecipe: Any,
    val recipeId: Int,
    val recipeImg: Any,
    val recipeName: String,
    val storeId: Int,
    val type: String
)