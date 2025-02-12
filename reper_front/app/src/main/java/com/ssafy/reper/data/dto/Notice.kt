package com.ssafy.reper.data.dto

data class Notice(
    val noticeId: Int,
    val title: String,
    val content: String,
    val updatedAt: String,
    val timeAgo: String
)