package com.ssafy.reper.data.dto

data class KakaoLoginRequest(
    val accessToken: String,
    val email: String,
    val nickname: String
)