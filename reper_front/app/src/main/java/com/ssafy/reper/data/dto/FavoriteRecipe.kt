package com.ssafy.reper.data.dto

data class FavoriteRecipe(
    val recipeId: Int,
    val recipeImg: Any? = null,
    val recipeName: String = ""
)