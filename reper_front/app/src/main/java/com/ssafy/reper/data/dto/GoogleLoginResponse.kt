package com.ssafy.reper.data.dto

data class GoogleLoginResponse (
    val userId: Int,
    val email: String,
    val userName: String,
    val phone: String,
    val role: String,
    val createdAt: List<Int>
)