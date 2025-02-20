package com.ssafy.smartstore_jetpack.util

import java.text.DecimalFormat

object CommonUtils {

    //천단위 콤마
    fun makeComma(num: Int): String {
        val comma = DecimalFormat("#,###")
        return "${comma.format(num)} 원"
    }

}