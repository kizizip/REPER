package com.ssafy.reper.data.dto

data class Recipe(
    val category: String,
    val ingredients: MutableList<Ingredient>,
    val recipeId: Int,
    val recipeImg: Any?,
    val recipeName: String,
    val recipeSteps: MutableList<RecipeStep>,
    val type: String
)