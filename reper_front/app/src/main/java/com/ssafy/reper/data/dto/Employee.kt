package com.ssafy.reper.data.dto

data class Employee(
    val email: String,
    val employed: Boolean,
    val phone: String,
    val role: String,
    val userId: Int,
    val userName: String
)