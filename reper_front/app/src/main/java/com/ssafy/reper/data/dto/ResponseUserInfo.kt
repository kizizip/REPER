package com.ssafy.reper.data.dto

import java.time.LocalDateTime

data class ResponseUserInfo(
    val email: String,
    val username: String,
    val phone: String,
    val userId: Long,
    val role: String,
    val createdAt: List<Int>,  // 배열로 받고
)