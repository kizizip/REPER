package com.ssafy.reper.data.dto

import java.sql.Timestamp
import java.time.LocalDateTime

class UserInfo (
    val userId: Long? = null, // Long?을 사용하여 null 가능하도록 처리
    val createdAt: LocalDateTime? = null,  // createdAt은 서버에서 자동으로 생성되므로 null 가능
    val email: String? = null,
    val password: String? = null,
    val phone: String? = null,
    val role: String? = null,
    val userName: String? = null,
    val message: String? = null
)