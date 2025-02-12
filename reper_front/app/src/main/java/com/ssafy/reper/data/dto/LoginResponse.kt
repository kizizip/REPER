package com.ssafy.reper.data.dto

data class LoginResponse (
    val role : String? = null,
    val loginIdCookie: String? = null,
    val userId: Long? = null,
    val username: String? = null
)