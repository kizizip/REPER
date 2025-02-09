package com.ssafy.reper.data.dto

data class Recipe(
    val category: String,
    val ingredients: List<Ingredient>,
    val recipeId: Int,
    val recipeImg: Any,
    val recipeName: String,
    val recipeSteps: List<RecipeStep>,
    val type: String
)