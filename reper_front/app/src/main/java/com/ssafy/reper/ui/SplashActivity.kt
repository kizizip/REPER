package com.ssafy.reper.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ssafy.reper.R
import com.ssafy.reper.data.local.SharedPreferencesUtil
import com.ssafy.reper.databinding.ActivitySplashBinding
import com.ssafy.reper.ui.login.LoginActivity

class SplashActivity : AppCompatActivity() {
    
    private lateinit var _binding: ActivitySplashBinding
    private val binding get() = _binding
    private val handler = Handler(Looper.getMainLooper())
    private var currentImage = true // true: before_move_coffe, false: after_move_coffe
    
    // 이미지 변경을 위한 Runnable
    private val imageRunnable = object : Runnable {
        override fun run() {
            // 이미지 토글
            currentImage = !currentImage
            binding.splashImageView.setImageResource(
                if (currentImage) R.drawable.before_move_coffe
                else R.drawable.after_move_coffe
            )
            // 0.5초 후 다시 실행
            handler.postDelayed(this, 500)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 초기 이미지 설정
        binding.splashImageView.setImageResource(R.drawable.before_move_coffe)
        
        // 0.5초 후 이미지 변경 애니메이션 시작
        handler.postDelayed(imageRunnable, 500)

        // 2초 후 화면 전환
        handler.postDelayed({
            val sharedPreferencesUtil = SharedPreferencesUtil(this)
            if (sharedPreferencesUtil.getUser().userId != -1) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            // 애니메이션 중단
            handler.removeCallbacks(imageRunnable)
            finish()
        }, 2000)
    }

    override fun onResume() {
        super.onResume()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // 화면 회전 잠금
    }

    override fun onDestroy() {
        super.onDestroy()
        // Activity 종료 시 애니메이션 중단
        handler.removeCallbacks(imageRunnable)
    }
}