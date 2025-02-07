package com.ssafy.reper.data.dto

import java.time.LocalDateTime

// 회원가입
data class JoinRequest (
    val email: String? = null,
    val password: String? = null,
    val userName: String? = null,
    val phone: String? = null,
    val role: String? = null,
)