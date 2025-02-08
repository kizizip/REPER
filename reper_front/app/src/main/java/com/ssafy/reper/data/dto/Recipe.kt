package com.ssafy.reper.data.dto

data class Recipe(
    val category: String,
    val ingredients: MutableList<Ingredient>,
    val recipeName: String,
    val recipeSteps: MutableList<RecipeStep>,
    val type: String
)