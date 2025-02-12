package com.ssafy.reper.data.dto

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id

data class OwnerStore (
    val storeId: Int? = null,
    val storeName: String? = null,
    val ownerId: Int? = null
)