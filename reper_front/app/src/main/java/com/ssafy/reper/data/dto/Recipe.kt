package com.ssafy.reper.data.dto

data class Recipe(
    val category: String,
    val createdAt: String,
    val ingredients: MutableList<Ingredient>,
    val orderDetails: MutableList<OrderDetail>,
    val recipeId: Int,
    val recipeImg: String,
    val recipeName: String,
    val recipeSteps: MutableList<RecipeStep>,
    val store: String,
    val type: String,
    val userFavoriteRecipes: MutableList<UserFavoriteRecipe>
)