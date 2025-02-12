package com.ssafy.reper

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

class FcmDialog(context: Context, private val title: String, private val message: String) :
    Dialog(context) {

    init {
        setContentView(R.layout.dialog_delete)  // XML 레이아웃 사용
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        findViewById<TextView>(R.id.dialog_delete_bold_tv).visibility = View.GONE
        findViewById<TextView>(R.id.dialog_delete_rle_tv).visibility = View.GONE
        val btnPositive = findViewById<Button>(R.id.dialog_delete_delete_btn)
        btnPositive.text = "확인"
        findViewById<Button>(R.id.dialog_delete_cancle_btn).visibility = View.GONE

        findViewById<TextView>(R.id.dialog_delete_tv).text = message

        btnPositive.setOnClickListener {
            dismiss()  // 확인 버튼 클릭 시 다이얼로그 닫기
        }
    }
}
