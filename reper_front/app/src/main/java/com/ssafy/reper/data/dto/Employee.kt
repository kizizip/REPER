package com.ssafy.reper.data.dto

data class Employee(
    val created_at: String,
    val email: String,
    val id: Int,
    val phone: Int,
    val role: String,
    val username: String
    //승인여부 없음
)