package com.ssafy.reper.data.dto

data class User(
    val email: String,
    val password: String,
    val phone: String,
    val role: String,
    val roleEnum: String,
    val userName: String
)