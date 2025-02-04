package com.ssafy.reper.data.dto

data class StoreNoticeResponse(
    val notices: List<Notice>,
    val storeId: Int
)