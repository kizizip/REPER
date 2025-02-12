package com.ssafy.reper.data.dto

data class NoticeRequest (
    val userId: Int,
    val title: String,
    val content: String
)