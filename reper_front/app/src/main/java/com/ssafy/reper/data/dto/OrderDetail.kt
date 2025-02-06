package com.ssafy.reper.data.dto

data class OrderDetail(
    val customerRequest: String,
    val orderDetailId: Int,
    val quantity: Int,
    val recipeId: Int
)