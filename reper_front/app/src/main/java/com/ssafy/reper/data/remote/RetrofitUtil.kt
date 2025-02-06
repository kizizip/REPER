package com.ssafy.reper.data.remote

import com.ssafy.smartstore_jetpack.base.ApplicationClass


class RetrofitUtil {
    companion object{
        val orderService = ApplicationClass.retrofit.create(OrderService::class.java)
    }
}