package com.ssafy.reper.data.dto

data class UserToken(
    val storeId: Int,
    val token: String,
    val userId: Int
)