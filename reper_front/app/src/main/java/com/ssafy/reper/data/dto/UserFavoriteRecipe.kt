package com.ssafy.reper.data.dto

data class UserFavoriteRecipe(
    val recipe: String,
    val user: User,
    val userFavoriteRecipeId: Int
)