package com.ssafy.reper.data.dto

data class Order(
    val completed: Boolean,
    val orderDate: String,
    val takeout: Boolean,
    val orderDetails: MutableList<OrderDetail>,
    val orderId: Int
)