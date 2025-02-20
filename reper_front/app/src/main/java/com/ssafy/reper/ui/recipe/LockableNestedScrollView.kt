package com.ssafy.reper.ui.recipe

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView

class LockableNestedScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : NestedScrollView(context, attrs, defStyle) {

    var isScrollable: Boolean = true

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        // isScrollable 이 false 면 터치 이벤트를 인터셉트하지 않음
        return if (isScrollable) super.onInterceptTouchEvent(ev) else false
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        // isScrollable 이 false 면 터치 이벤트를 처리하지 않음
        return if (isScrollable) super.onTouchEvent(ev) else false
    }
}