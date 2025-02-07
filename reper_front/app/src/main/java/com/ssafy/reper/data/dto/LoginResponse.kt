package com.ssafy.reper.data.dto

data class LoginResponse (
    val role : String,
    val loginIdCookie: String,
    val userId: Int,
    val username: String
)