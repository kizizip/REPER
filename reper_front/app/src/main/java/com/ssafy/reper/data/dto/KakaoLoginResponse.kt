package com.ssafy.reper.data.dto

data class KakaoLoginResponse(
    val userId: Int?, // Long?을 사용하여 null 가능하도록 처리
    val email: String,
    val password: String?,
    val userName: String,
    val phone: String?,
    val role: String,
    val createdAt: List<Int>, // createdAt은 서버에서 자동으로 생성되므로 null 가능
    val stores: List<Any>?,
    val storeEmployees: List<Any>?,
    val userFavoriteRecipes: List<Any>?
)