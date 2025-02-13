package com.ssafy.reper.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ssafy.reper.R
import com.ssafy.reper.data.local.SharedPreferencesUtil
import com.ssafy.reper.databinding.ActivityLoginBinding
import com.ssafy.reper.ui.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // 자동 로그인 체크
        checkAutoLogin()

        // View Binding 초기화
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // LoginFragment 표시
        supportFragmentManager.beginTransaction()
            .replace(R.id.activityLoginFragmentContainer, LoginFragment())
            .commit()
    }

    private fun checkAutoLogin() {
        val sharedPreferencesUtil = SharedPreferencesUtil(this)
        
        // 세션이 유효하고 사용자 정보가 있는 경우
        if (sharedPreferencesUtil.getUser().userId != -1) {
            // MainActivity로 이동
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            // 세션이 만료되었거나 사용자 정보가 없는 경우
            sharedPreferencesUtil.clearUserData()
        }
    }
}