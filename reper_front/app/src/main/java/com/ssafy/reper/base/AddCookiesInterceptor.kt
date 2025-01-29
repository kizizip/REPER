//package com.ssafy.smartstore_jetpack.base
//
//import okhttp3.Interceptor
//import okhttp3.Request
//import okhttp3.Response
//import java.io.IOException
//
//private const val TAG = "AddCookiesInter_싸피"
//class AddCookiesInterceptor : Interceptor{
//
//    @Throws(IOException::class)
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val builder: Request.Builder = chain.request().newBuilder()
//
//        // cookie 가져오기
//        val getCookies = ApplicationClass.sharedPreferencesUtil.getUserCookie()
//        for (cookie in getCookies!!) {
//            builder.addHeader("Cookie", cookie)
////            Log.v(TAG,"Adding Header: $cookie")
//        }
//        return chain.proceed(builder.build())
//    }
//}

package com.ssafy.reper.base

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

private const val TAG = "AddCookiesInter_싸피"
class AddCookiesInterceptor : Interceptor{

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder: Request.Builder = chain.request().newBuilder()

        // 저장된 쿠키 불러오기
        val preferences = ApplicationClass.sharedPreferencesUtil
        val cookie = preferences.getUserCookie()

        if (cookie != null) {
            builder.addHeader("Cookie", cookie)
//            Log.v(TAG,"Adding Header: $cookie")
        }
        return chain.proceed(builder.build())
    }
}