package com.ssafy.reper.ui.login

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
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

        val sharedPreferencesUtil = SharedPreferencesUtil(this)
        
        // 자동 로그인 체크
        if (sharedPreferencesUtil.isCookieValid() && sharedPreferencesUtil.getUserCookie() != null) {
            // 메인 액티비티로 이동
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        // View Binding 초기화
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // LoginFragment 표시
        supportFragmentManager.beginTransaction()
            .replace(R.id.activityLoginFragmentContainer, LoginFragment())
            .commit()
    }
}