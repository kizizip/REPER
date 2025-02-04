package com.ssafy.reper.ui.login

import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ssafy.reper.R
import com.ssafy.reper.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // View Binding 초기화
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 초기 Fragment 설정 ( 앱 시작시 LoginActivity가 뜨게)
        // 추후 로그인 되어있는지 확인하고 되어있다면 바로 MainActivity로 넘어가게 만들어야함 - (추후 수정 필요)
        supportFragmentManager.beginTransaction()
            .replace(R.id.activityLoginFragmentContainer, LoginFragment())
            .commit()

    }
}