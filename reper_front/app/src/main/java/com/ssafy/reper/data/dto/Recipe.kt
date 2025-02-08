package com.ssafy.reper.data.dto

data class Recipe(

    val recipeId: Int,
    val category: String,
    val ingredients: List<Ingredient>,
    val recipeName: String,
    val recipeSteps: List<RecipeStep>,
    val type: String

)