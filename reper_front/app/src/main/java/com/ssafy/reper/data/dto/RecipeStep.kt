package com.ssafy.reper.data.dto

data class RecipeStep(
    val animationUrl: String,
    val createdAt: String,
    val instruction: String,
    val recipe: String,
    val recipeStepId: Int,
    val stepNumber: Int,
    val updatedAt: String
)