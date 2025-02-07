package com.ssafy.reper.data.remote

import ApplicationClass

class RetrofitUtil {
    companion object{
        val noticeService = ApplicationClass.retrofit.create(NotiService::class.java)
        val bossService = ApplicationClass.retrofit.create(BossService::class.java)

    }
}